package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;
import com.college.timetable.scheduler.load.core.DivisionScheduleMatrix;
import com.college.timetable.scheduler.load.core.FacultyScheduleMatrix;
import com.college.timetable.scheduler.load.core.SchedulingTask;
import com.college.timetable.scheduler.load.core.SlotConstants;

import org.springframework.stereotype.Service;

import java.util.Map;

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

    public boolean isLunchSlot(Slot slot) {
        if (slot == null) {
            return false;
        }
        return (slot.getSlotNumber() - 1) == SlotConstants.LUNCH_SLOT;
    }

    public boolean divisionDailyLimitReached(Division division, String day, int maxLecturesPerDay) {
        if (division == null || day == null) {
            return false;
        }
        long count = division.getTimetable().keySet().stream()
                .filter(slot -> day.equals(slot.getDay()))
                .count();
        return count >= maxLecturesPerDay;
    }

    // ===== Load scheduler helpers =====
    public boolean isFacultyAvailable(String faculty, int day, int slot,
                                      Map<String, FacultyScheduleMatrix> facultyMatrices) {

        FacultyScheduleMatrix matrix = facultyMatrices.get(faculty);
        return matrix == null || matrix.isFree(day, slot);
    }

    public boolean isDivisionFree(String division, int day, int slot,
                                  Map<String, DivisionScheduleMatrix> divisionMatrices) {

        DivisionScheduleMatrix matrix = divisionMatrices.get(division);
        return matrix == null || matrix.isFree(day, slot);
    }

    public boolean subjectLimitReached(String division, String subject, int day,
                                       Map<String, DivisionScheduleMatrix> divisionMatrices) {

        DivisionScheduleMatrix divisionMatrix = divisionMatrices.get(division);
        if (divisionMatrix == null) {
            return false;
        }

        int count = 0;
        for (int s = 0; s < SlotConstants.SLOTS_PER_DAY; s++) {
            SchedulingTask task = divisionMatrix.getTask(day, s);
            if (task != null && task.getSubject().equals(subject)) {
                count++;
            }
        }

        return count >= 2;
    }

    public boolean subjectAlreadyScheduledToday(DivisionScheduleMatrix matrix,
                                                String subject,
                                                int day) {
        if (matrix == null) {
            return false;
        }
        return matrix.subjectAlreadyScheduledToday(subject, day);
    }

    public boolean divisionDailyLimitReached(DivisionScheduleMatrix matrix, int day) {
        if (matrix == null) {
            return false;
        }
        return matrix.getDayLoad(day) >= SlotConstants.MAX_DIVISION_LECTURES_PER_DAY;
    }

    public boolean facultyDailyLimitReached(FacultyScheduleMatrix matrix, int day, int requiredSlots) {
        if (matrix == null) {
            return false;
        }
        return !matrix.canTakeMore(day, requiredSlots);
    }

    public boolean facultySlotOccupied(String faculty,
                                       int day,
                                       int slot,
                                       Map<String, FacultyScheduleMatrix> facultyMatrices) {
        FacultyScheduleMatrix matrix = facultyMatrices.get(faculty);
        return matrix != null && !matrix.isFree(day, slot);
    }
}
