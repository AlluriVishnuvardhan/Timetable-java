package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotAllocationService {

    // ✅ THEORY
    public TimetableSlot allocateTheory(Division division,
                                        Slot slot,
                                        Subject subject,
                                        Faculty faculty) {

        TimetableSlot entry =
                new TimetableSlot(subject, faculty, slot, null);

        division.assignSlot(slot, entry);

        return entry;
    }


    // ✅ LAB
    public List<TimetableSlot> allocateLab(Division division,
                                           Slot slot1,
                                           Slot slot2,
                                           Subject subject,
                                           Faculty faculty,
                                           String batchName) {

        TimetableSlot entry1 =
                new TimetableSlot(subject, faculty, slot1, batchName);

        TimetableSlot entry2 =
                new TimetableSlot(subject, faculty, slot2, batchName);

        division.assignSlot(slot1, entry1);
        division.assignSlot(slot2, entry2);

        return List.of(entry1, entry2);
    }
}