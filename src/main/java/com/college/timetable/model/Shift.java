package com.college.timetable.model;

import java.time.LocalTime;

public class Shift {

    private LocalTime startTime;
    private LocalTime endTime;

    public Shift(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isWithinShift(Slot slot) {
        return !slot.getStartTime().isBefore(startTime)
                && !slot.getEndTime().isAfter(endTime);
    }
}