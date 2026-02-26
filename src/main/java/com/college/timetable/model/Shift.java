package com.college.timetable.model;

import java.time.LocalTime;

public class Shift {

    private LocalTime shiftStart;
    private LocalTime shiftEnd;

    public Shift(LocalTime shiftStart, LocalTime shiftEnd) {
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
    }

    public LocalTime getShiftStart() {
        return shiftStart;
    }

    public LocalTime getShiftEnd() {
        return shiftEnd;
    }

    // ✅ Check if a given slot time is inside faculty shift
    public boolean isWithinShift(LocalTime time) {
        return !time.isBefore(shiftStart)
                && !time.isAfter(shiftEnd);
    }
}