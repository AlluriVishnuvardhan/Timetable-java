package com.college.timetable.scheduler.load.core;

public class FacultyScheduleMatrix {

    private final boolean[][] matrix;

    public FacultyScheduleMatrix() {
        matrix = new boolean[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY];
    }

    public boolean isFree(int day, int slot) {
        return !matrix[day][slot];
    }

    public void assign(int day, int slot) {
        matrix[day][slot] = true;
    }

    public void unassign(int day, int slot) {
        matrix[day][slot] = false;
    }

    public boolean[][] getMatrix() {
        return matrix;
    }
}