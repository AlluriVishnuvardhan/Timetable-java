package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacultyAllocationService {

    private static final int MAX_LECTURES_PER_DAY = 4;
    private final Map<String, Map<String, Integer>> facultyDailyLoad = new HashMap<>();

    public boolean canAllocate(Faculty faculty, Slot slot) {
        if (faculty == null || slot == null) {
            return false;
        }
        if (!faculty.isAvailable(slot)) {
            return false;
        }

        Map<String, Integer> dayMap = facultyDailyLoad.computeIfAbsent(
                faculty.getName(), k -> new HashMap<>());
        int current = dayMap.getOrDefault(slot.getDay(), 0);
        return current < MAX_LECTURES_PER_DAY;
    }

    public boolean allocate(Faculty faculty, Slot slot) {
        if (!canAllocate(faculty, slot)) {
            return false;
        }

        Map<String, Integer> dayMap = facultyDailyLoad.get(faculty.getName());
        int current = dayMap.getOrDefault(slot.getDay(), 0);
        faculty.occupySlot(slot);
        dayMap.put(slot.getDay(), current + 1);
        return true;
    }
}
