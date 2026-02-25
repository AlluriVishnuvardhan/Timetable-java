package com.college.timetable.model;

public class Subject {

    private String name;
    private boolean isLab;
    private int weeklyHours;

    public Subject(String name, boolean isLab, int weeklyHours) {
        this.name = name;
        this.isLab = isLab;
        this.weeklyHours = weeklyHours;
    }

    public boolean isLab() {
        return isLab;
    }

    public String getName() {
        return name;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }
}