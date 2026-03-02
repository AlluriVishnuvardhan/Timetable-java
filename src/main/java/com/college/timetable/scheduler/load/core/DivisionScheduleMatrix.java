package com.college.timetable.scheduler.load.core;

import com.college.timetable.model.TimetableSlot;

public class DivisionScheduleMatrix {

    private final TimetableSlot[][] matrix;

    public DivisionScheduleMatrix() {
        matrix = new TimetableSlot[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY];
    }

    public boolean isFree(int day, int slot) {
        return matrix[day][slot] == null;
    }

    public void assign(int day, int slot, TimetableSlot timetableSlot) {
        matrix[day][slot] = timetableSlot;
    }

    public void unassign(int day, int slot) {
        matrix[day][slot] = null;
    }

    public TimetableSlot get(int day, int slot) {
        return matrix[day][slot];
    }

    public TimetableSlot[][] getMatrix() {
        return matrix;
    }
}