package com.college.timetable.model;

public class TimetableSlot {

    private Subject subject;
    private Faculty faculty;
    private Slot assignedSlot;   // ✅ ADD THIS
    private String batchName;

    public TimetableSlot(Subject subject,
                         Faculty faculty,
                         Slot assignedSlot,
                         String batchName) {
        this.subject = subject;
        this.faculty = faculty;
        this.assignedSlot = assignedSlot;
        this.batchName = batchName;
    }

    public Subject getSubject() {
        return subject;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public Slot getAssignedSlot() {   // ✅ ADD THIS
        return assignedSlot;
    }

    public String getBatchName() {
        return batchName;
    }
}