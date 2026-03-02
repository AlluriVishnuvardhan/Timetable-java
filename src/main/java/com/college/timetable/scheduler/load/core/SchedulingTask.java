package com.college.timetable.scheduler.load.core;

public class SchedulingTask {

    private String facultyCode;
    private String facultyName;

    private String year;
    private String division;

    private String subject;
    private TaskType type;

    private int requiredCount;      // Always 1 per task unit
    private int allocatedCount = 0;

    private boolean requiresContinuous; // true for lab
    private String facultyShift;
    private String divisionShift;

    private Day divisionHoliday;

    public SchedulingTask(String facultyCode,
                          String facultyName,
                          String year,
                          String division,
                          String subject,
                          TaskType type,
                          boolean requiresContinuous,
                          String facultyShift,
                          String divisionShift,
                          Day divisionHoliday) {

        this.facultyCode = facultyCode;
        this.facultyName = facultyName;
        this.year = year;
        this.division = division;
        this.subject = subject;
        this.type = type;
        this.requiresContinuous = requiresContinuous;
        this.facultyShift = facultyShift;
        this.divisionShift = divisionShift;
        this.divisionHoliday = divisionHoliday;
        this.requiredCount = 1;
    }

    public boolean isAllocated() {
        return allocatedCount >= requiredCount;
    }

    public void markAllocated() {
        allocatedCount++;
    }

    public void undoAllocation() {
        allocatedCount--;
    }

    public int getMissingCount() {
        return requiredCount - allocatedCount;
    }

    // Getters

    public String getFacultyCode() { return facultyCode; }
    public String getFacultyName() { return facultyName; }
    public String getYear() { return year; }
    public String getDivision() { return division; }
    public String getSubject() { return subject; }
    public TaskType getType() { return type; }
    public boolean isRequiresContinuous() { return requiresContinuous; }
    public String getFacultyShift() { return facultyShift; }
    public String getDivisionShift() { return divisionShift; }
    public Day getDivisionHoliday() { return divisionHoliday; }
}