package com.college.timetable.scheduler.stage;

import com.college.timetable.config.SlotConfig;
import com.college.timetable.model.*;
import com.college.timetable.scheduler.pipeline.SchedulingStage;
import com.college.timetable.scheduler.service.ConflictCheckerService;
import com.college.timetable.scheduler.service.FacultyAllocationService;
import com.college.timetable.scheduler.service.SlotAllocationService;
import com.college.timetable.service.TimetableContext;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LabSchedulingStage implements SchedulingStage {

    private final ConflictCheckerService conflictChecker;
    private final FacultyAllocationService facultyAllocationService;
    private final SlotAllocationService slotAllocationService;
    private final SlotConfig slotConfig;

    public LabSchedulingStage(ConflictCheckerService conflictChecker,
                              FacultyAllocationService facultyAllocationService,
                              SlotAllocationService slotAllocationService,
                              SlotConfig slotConfig) {
        this.conflictChecker = conflictChecker;
        this.facultyAllocationService = facultyAllocationService;
        this.slotAllocationService = slotAllocationService;
        this.slotConfig = slotConfig;
    }

    @Override
    public void execute(TimetableContext context) {

        List<Division> divisions = context.getDivisions();
        List<Faculty> faculties = context.getFaculties();
        List<Subject> subjects = context.getSubjects();
        List<Slot> allSlots = context.getAllSlots();

        for (Division division : divisions) {

            for (Subject subject : subjects) {

                // 🔥 Only process LAB subjects
                if (!subject.isLab()) continue;

                boolean allocated = false;

                for (Slot slot : allSlots) {

                    Slot nextSlot = slotConfig.getNextSlot(slot);

                    // Ensure slots are continuous
                    if (nextSlot == null || !slotConfig.areContinuous(slot, nextSlot))
                        continue;

                    // Check division free
                    if (!conflictChecker.isDivisionSlotFree(division, slot))
                        continue;

                    if (!conflictChecker.isDivisionSlotFree(division, nextSlot))
                        continue;

                    // Find available faculty
                    Faculty availableFaculty =
                            findAvailableFaculty(faculties, slot, nextSlot);

                    if (availableFaculty != null) {

                        // 🔥 LOCK FACULTY FIRST
                        facultyAllocationService.allocate(availableFaculty, slot);
                        facultyAllocationService.allocate(availableFaculty, nextSlot);

                        // 🔥 Allocate Lab (Now Returns List<TimetableSlot>)
                        List<TimetableSlot> labSlots =
                                slotAllocationService.allocateLab(
                                        division,
                                        slot,
                                        nextSlot,
                                        subject,
                                        availableFaculty,
                                        "BATCH-ALL"
                                );

                        // 🔥 ADD TO CONTEXT (IMPORTANT FIX)
                        context.getTimetableSlots().addAll(labSlots);

                        allocated = true;
                        break;
                    }
                }

                if (!allocated) {
                    System.out.println("WARNING: Could not allocate lab for "
                            + subject.getName()
                            + " in division "
                            + division.getName());
                }
            }
        }
    }

    private Faculty findAvailableFaculty(List<Faculty> faculties,
                                         Slot slot1,
                                         Slot slot2) {

        for (Faculty faculty : faculties) {

            if (conflictChecker.canAssignFaculty(faculty, slot1)
                    && conflictChecker.canAssignFaculty(faculty, slot2)) {
                return faculty;
            }
        }

        return null;
    }
}