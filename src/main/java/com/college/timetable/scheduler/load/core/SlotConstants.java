package com.college.timetable.scheduler.load.core;

public class SlotConstants {

    public static final int DAYS_PER_WEEK = 6;
    public static final int SLOTS_PER_DAY = 6; // Lunch excluded

    // Morning shift slot timings
    public static final String[] MORNING_TIMES = {
            "08:00-09:00",
            "09:00-10:00",
            "10:00-11:00",
            "11:00-12:00",
            "13:00-14:00",
            "14:00-15:00"
    };

    // Afternoon shift slot timings
    public static final String[] AFTERNOON_TIMES = {
            "10:00-11:00",
            "11:00-12:00",
            "13:00-14:00",
            "14:00-15:00",
            "15:00-16:00",
            "16:00-17:00"
    };
}