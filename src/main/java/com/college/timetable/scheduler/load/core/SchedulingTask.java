package com.college.timetable.scheduler.load.core;

public class SchedulingTask {

    private String facultyCode;
    private String facultyName;

    private String year;
    private String semester;
    private String division;

    private String subject;
    private TaskType type;
    private String batch;

    // Required vs allocated tracking
    private int requiredCount;
    private int allocatedCount = 0;

    private boolean requiresContinuous; // true for lab

    private String facultyShift;
    private String divisionShift;

    private Day divisionHoliday;
    private String unscheduledReason;

    public SchedulingTask(String facultyCode,
                          String facultyName,
                          String year,
                          String semester,
                          String division,
                          String subject,
                          TaskType type,
                          boolean requiresContinuous,
                          String facultyShift,
                          String divisionShift,
                          String batch,
                          Day divisionHoliday) {

        this.facultyCode = facultyCode;
        this.facultyName = facultyName;
        this.year = year;
        this.semester = semester;
        this.division = division;
        this.subject = subject;
        this.type = type;
        this.requiresContinuous = requiresContinuous;
        this.facultyShift = facultyShift;
        this.divisionShift = divisionShift;
        this.batch = batch;
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
        if (allocatedCount > 0) {
            allocatedCount--;
        }
    }

    public int getMissingCount() {
        return requiredCount - allocatedCount;
    }

    public void setUnscheduledReason(String unscheduledReason) {
        this.unscheduledReason = unscheduledReason;
    }

    // ===== Getters =====

    public String getFacultyCode() {
        return facultyCode;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getYear() {
        return year;
    }

    public String getDivision() {
        return division;
    }

    public String getSemester() {
        return semester;
    }

    public String getSubject() {
        return subject;
    }

    public TaskType getType() {
        return type;
    }

    public String getBatch() {
        return batch;
    }

    public boolean isRequiresContinuous() {
        return requiresContinuous;
    }

    public String getFacultyShift() {
        return facultyShift;
    }

    public String getDivisionShift() {
        return divisionShift;
    }

    public Day getDivisionHoliday() {
        return divisionHoliday;
    }

    public String getUnscheduledReason() {
        return unscheduledReason;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public int getAllocatedCount() {
        return allocatedCount;
    }
}
