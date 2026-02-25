package com.college.timetable.service;

import com.college.timetable.model.*;

import java.util.List;
import java.util.ArrayList;

public class TimetableContext {

    private List<Division> divisions;
    private List<Faculty> faculties;
    private List<Subject> subjects;
    private List<Slot> allSlots;

    // 🔥 NEW: Store generated timetable slots
    private List<TimetableSlot> timetableSlots = new ArrayList<>();

    public TimetableContext(List<Division> divisions,
                            List<Faculty> faculties,
                            List<Subject> subjects,
                            List<Slot> allSlots) {
        this.divisions = divisions;
        this.faculties = faculties;
        this.subjects = subjects;
        this.allSlots = allSlots;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    public List<Faculty> getFaculties() {
        return faculties;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public List<Slot> getAllSlots() {
        return allSlots;
    }

    // ✅ NEW Getter
    public List<TimetableSlot> getTimetableSlots() {
        return timetableSlots;
    }

    // ✅ NEW Setter
    public void setTimetableSlots(List<TimetableSlot> timetableSlots) {
        this.timetableSlots = timetableSlots;
    }
}