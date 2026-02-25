package com.college.timetable.model;

import java.util.HashMap;
import java.util.Map;

public class Division {

    private String name;
    private Map<Slot, TimetableSlot> timetable = new HashMap<>();

    public Division(String name) {
        this.name = name;
    }

    public boolean isSlotFree(Slot slot) {
        return !timetable.containsKey(slot);
    }

    public void assignSlot(Slot slot, TimetableSlot entry) {
        timetable.put(slot, entry);
    }

    public Map<Slot, TimetableSlot> getTimetable() {
        return timetable;
    }

    public String getName() {
        return name;
    }
}