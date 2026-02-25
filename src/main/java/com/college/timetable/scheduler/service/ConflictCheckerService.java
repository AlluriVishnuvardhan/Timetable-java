package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;

import org.springframework.stereotype.Service;

@Service
public class ConflictCheckerService {

    // Check if faculty can take this slot
    public boolean canAssignFaculty(Faculty faculty, Slot slot) {
        return faculty.isAvailable(slot);
    }

    // Check if division slot is free
    public boolean isDivisionSlotFree(Division division, Slot slot) {
        return division.isSlotFree(slot);
    }

    // Check if subject already scheduled on same day (Theory rule)
    public boolean isSubjectScheduledSameDay(Division division,
                                             Subject subject,
                                             String day) {

        return division.getTimetable().entrySet().stream()
                .anyMatch(entry ->
                        entry.getKey().getDay().equals(day)
                        && entry.getValue().getSubject().getName()
                        .equals(subject.getName()));
    }
}