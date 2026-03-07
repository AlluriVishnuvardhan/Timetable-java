package com.college.timetable.scheduler.stage;

import com.college.timetable.model.*;
import com.college.timetable.scheduler.pipeline.SchedulingStage;
import com.college.timetable.scheduler.service.ConflictCheckerService;
import com.college.timetable.scheduler.service.FacultyAllocationService;
import com.college.timetable.scheduler.service.SlotAllocationService;
import com.college.timetable.service.TimetableContext;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TheorySchedulingStage implements SchedulingStage {

    private final ConflictCheckerService conflictChecker;
    private final FacultyAllocationService facultyAllocationService;
    private final SlotAllocationService slotAllocationService;

    public TheorySchedulingStage(ConflictCheckerService conflictChecker,
                                 FacultyAllocationService facultyAllocationService,
                                 SlotAllocationService slotAllocationService) {
        this.conflictChecker = conflictChecker;
        this.facultyAllocationService = facultyAllocationService;
        this.slotAllocationService = slotAllocationService;
    }

    @Override
    public void execute(TimetableContext context) {

        List<Division> divisions = context.getDivisions();
        List<Faculty> faculties = context.getFaculties();
        List<Subject> subjects = context.getSubjects();
        List<Slot> allSlots = context.getAllSlots();

        for (Division division : divisions) {

            for (Subject subject : subjects) {

                if (subject.isLab()) continue;

                int hoursToAllocate = subject.getWeeklyHours();

                // Track how many times subject scheduled per day
                Map<String, Integer> dayCount = new HashMap<>();

                for (Slot slot : allSlots) {

                    if (hoursToAllocate <= 0) break;
                    if (slotAllocationService.isLunchSlot(slot))
                        continue;

                    if (!conflictChecker.isDivisionSlotFree(division, slot))
                        continue;
                    String day = slot.getDay();
                    if (conflictChecker.divisionDailyLimitReached(division, day, 6))
                        continue;

                    int countToday = dayCount.getOrDefault(day, 0);

                    // Same subject should not repeat on same day.
                    if (countToday >= 1)
                        continue;

                    Faculty availableFaculty =
                            findAvailableFaculty(faculties, slot);

                    if (availableFaculty != null) {

                        if (!facultyAllocationService.allocate(availableFaculty, slot)) {
                            continue;
                        }

                        TimetableSlot entry = slotAllocationService.allocateTheory(
                                division,
                                slot,
                                subject,
                                availableFaculty
                        );
                        if (entry == null) {
                            continue;
                        }
                        context.getTimetableSlots().add(entry);

                        dayCount.put(day, countToday + 1);

                        hoursToAllocate--;
                    }
                }

                if (hoursToAllocate > 0) {
                    System.out.println("WARNING: Could not fully allocate theory for "
                            + subject.getName()
                            + " in division "
                            + division.getName());
                }
            }
        }
    }

    private Faculty findAvailableFaculty(List<Faculty> faculties,
                                         Slot slot) {

        for (Faculty faculty : faculties) {

            if (conflictChecker.canAssignFaculty(faculty, slot)) {
                return faculty;
            }
        }

        return null;
    }
}
