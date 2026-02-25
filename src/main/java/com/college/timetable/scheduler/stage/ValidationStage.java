package com.college.timetable.scheduler.stage;

import com.college.timetable.model.*;
import com.college.timetable.scheduler.pipeline.SchedulingStage;
import com.college.timetable.service.TimetableContext;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ValidationStage implements SchedulingStage {

    @Override
    public void execute(TimetableContext context) {

        validateDivisionLoad(context.getDivisions());
        validateFacultyConflicts(context.getFaculties());
        validateLabContinuity(context.getDivisions());

        System.out.println("Validation completed successfully.");
    }

    private void validateDivisionLoad(List<Division> divisions) {

        for (Division division : divisions) {

            int totalHours = division.getTimetable().size();

            if (totalHours > 30) {
                System.out.println("ERROR: Division "
                        + division.getName()
                        + " exceeds 30 teaching hours.");
            }
        }
    }

    private void validateFacultyConflicts(List<Faculty> faculties) {

        for (Faculty faculty : faculties) {

            Set<Slot> occupied = faculty.getOccupiedSlots();

            if (occupied.size() != new HashSet<>(occupied).size()) {
                System.out.println("ERROR: Faculty double booking detected for "
                        + faculty.getName());
            }
        }
    }

    private void validateLabContinuity(List<Division> divisions) {

        for (Division division : divisions) {

            Map<Slot, TimetableSlot> timetable = division.getTimetable();

            for (Map.Entry<Slot, TimetableSlot> entry : timetable.entrySet()) {

                TimetableSlot slotEntry = entry.getValue();

                if (slotEntry.getSubject().isLab()) {

                    // Simple check: labs should appear at least twice same day
                    long countSameDay = timetable.entrySet().stream()
                            .filter(e ->
                                    e.getKey().getDay().equals(entry.getKey().getDay())
                                            && e.getValue().getSubject().getName()
                                            .equals(slotEntry.getSubject().getName()))
                            .count();

                    if (countSameDay < 2) {
                        System.out.println("WARNING: Lab continuity issue in division "
                                + division.getName()
                                + " for subject "
                                + slotEntry.getSubject().getName());
                    }
                }
            }
        }
    }
}