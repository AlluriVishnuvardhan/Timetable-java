package com.college.timetable.model;

public class TimetableSlot {

    private Subject subject;
    private Faculty faculty;
    private String batchName; // null for theory

    public TimetableSlot(Subject subject, Faculty faculty, String batchName) {
        this.subject = subject;
        this.faculty = faculty;
        this.batchName = batchName;
    }

    public Subject getSubject() {
        return subject;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public String getBatchName() {
        return batchName;
    }
}