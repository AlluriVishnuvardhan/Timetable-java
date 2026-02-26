package com.college.timetable.model;

import java.time.LocalTime;

public class Slot {

    private String day;
    private int slotNumber;
    private LocalTime startTime;
    private LocalTime endTime;

    public Slot(String day,
                int slotNumber,
                LocalTime startTime,
                LocalTime endTime) {

        this.day = day;
        this.slotNumber = slotNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}