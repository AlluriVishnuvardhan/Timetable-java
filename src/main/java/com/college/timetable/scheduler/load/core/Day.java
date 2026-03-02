package com.college.timetable.scheduler.load.core;

public enum Day {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5);

    private final int index;

    Day(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
