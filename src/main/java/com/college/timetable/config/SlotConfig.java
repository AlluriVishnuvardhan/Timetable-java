package com.college.timetable.config;
import jakarta.annotation.PostConstruct;

import com.college.timetable.model.Slot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SlotConfig {

    private final List<Slot> allSlots = new ArrayList<>();

    // This will be used later to generate 5x6 slots
    public List<Slot> getAllSlots() {
        return allSlots;
    }
    @PostConstruct
public void generateSlots() {

    String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

    int startHour = 8; // Morning base (we’ll validate shift separately)

    for (String day : days) {
        for (int i = 1; i <= 6; i++) {

            int hour = startHour + (i - 1);

            Slot slot = new Slot(
                    day,
                    i,
                    java.time.LocalTime.of(hour, 0),
                    java.time.LocalTime.of(hour + 1, 0)
            );

            allSlots.add(slot);
        }
    }
}

    // Check if two slots are continuous
    public boolean areContinuous(Slot s1, Slot s2) {
        if (s1 == null || s2 == null) {
            return false;
        }

        return s1.getDay().equals(s2.getDay())
                && s2.getSlotNumber() == s1.getSlotNumber() + 1;
    }

    // Get next slot in same day
    public Slot getNextSlot(Slot currentSlot) {

        if (currentSlot == null) return null;

        for (Slot slot : allSlots) {
            if (slot.getDay().equals(currentSlot.getDay())
                    && slot.getSlotNumber() == currentSlot.getSlotNumber() + 1) {
                return slot;
            }
        }

        return null;
    }
}