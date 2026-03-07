package com.college.timetable.scheduler.load.core;

public class FacultyScheduleMatrix {

    private final SchedulingTask[][] matrix;
    private final int[] dayLoad;

    public FacultyScheduleMatrix() {
        matrix = new SchedulingTask[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY];
        dayLoad = new int[SlotConstants.DAYS_PER_WEEK];
    }

    public boolean isFree(int day, int slot) {
        return matrix[day][slot] == null;
    }

    public void assign(int day, int slot, SchedulingTask task) {
        if (matrix[day][slot] == null) {
            dayLoad[day]++;
        }
        matrix[day][slot] = task;
    }

    public void unassign(int day, int slot) {
        if (matrix[day][slot] != null) {
            dayLoad[day]--;
        }
        matrix[day][slot] = null;
    }

    public boolean canTakeMore(int day, int requiredSlots) {
        return dayLoad[day] + requiredSlots <= SlotConstants.MAX_FACULTY_LECTURES_PER_DAY;
    }

    public SchedulingTask[][] getMatrix() {
        return matrix;
    }

    public int getRows() {
        return matrix.length;
    }

    public int getCols() {
        return matrix[0].length;
    }

    public String getCell(int row, int col) {

        SchedulingTask task = matrix[row][col];

        if (task == null) {
            return "";
        }

        return task.getSubject() + " (" + task.getDivision() + ")";
    }
}
