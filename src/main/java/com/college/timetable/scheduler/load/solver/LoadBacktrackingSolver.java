package com.college.timetable.scheduler.load.solver;

import com.college.timetable.scheduler.load.core.Day;
import com.college.timetable.scheduler.load.core.DivisionScheduleMatrix;
import com.college.timetable.scheduler.load.core.FacultyScheduleMatrix;
import com.college.timetable.scheduler.load.core.SchedulingTask;
import com.college.timetable.scheduler.load.core.SlotConstants;
import com.college.timetable.scheduler.load.core.TaskType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class LoadBacktrackingSolver {

    private final List<SchedulingTask> tasks;
    private final Map<String, DivisionScheduleMatrix> divisionMatrices = new HashMap<>();
    private final Map<String, FacultyScheduleMatrix> facultyMatrices = new HashMap<>();
    private final Map<String, String[][]> divisionSchedule = new HashMap<>();
    private final Map<String, String[][]> facultySchedule = new HashMap<>();
    private final Map<String, Map<String, int[]>> subjectDailyCount = new HashMap<>();
    private final List<SchedulingTask> unscheduledTasks = new ArrayList<>();

    private static final DateTimeFormatter[] SHIFT_FORMATTERS = {
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("hh:mm a")
    };

    public LoadBacktrackingSolver(List<SchedulingTask> tasks) {
        this.tasks = tasks;
    }

    public void solve() {
        for (SchedulingTask task : tasks) {
            divisionMatrices.computeIfAbsent(getDivisionKey(task), k -> new DivisionScheduleMatrix());
            facultyMatrices.computeIfAbsent(task.getFacultyCode(), k -> new FacultyScheduleMatrix());
            divisionSchedule.computeIfAbsent(getDivisionKey(task), k -> new String[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY]);
            facultySchedule.computeIfAbsent(task.getFacultyCode(), k -> new String[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY]);
        }

        List<SchedulingTask> labTasks = new ArrayList<>();
        List<SchedulingTask> theoryTasks = new ArrayList<>();
        for (SchedulingTask task : tasks) {
            if (task.getType() == TaskType.LAB) {
                labTasks.add(task);
            } else {
                theoryTasks.add(task);
            }
        }

        scheduleLabs(labTasks);
        scheduleTheory(theoryTasks);
        collectUnscheduled();
    }

    private void scheduleLabs(List<SchedulingTask> labTasks) {
        Map<String, List<SchedulingTask>> byDivision = new LinkedHashMap<>();
        for (SchedulingTask task : labTasks) {
            byDivision.computeIfAbsent(getDivisionKey(task), key -> new ArrayList<>()).add(task);
        }

        for (Map.Entry<String, List<SchedulingTask>> entry : byDivision.entrySet()) {
            List<List<SchedulingTask>> rounds = buildLabRounds(entry.getValue());
            for (List<SchedulingTask> round : rounds) {
                if (!placeLabRound(entry.getKey(), round)) {
                    for (SchedulingTask task : round) {
                        task.setUnscheduledReason("No common 2-slot lab window for all batches");
                    }
                }
            }
        }
    }

    private void scheduleTheory(List<SchedulingTask> theoryTasks) {
        List<SchedulingTask> remaining = new ArrayList<>();
        for (SchedulingTask task : theoryTasks) {
            if (!tryPlaceTheory(task, 1)) {
                remaining.add(task);
            }
        }

        for (SchedulingTask task : remaining) {
            if (!tryPlaceTheory(task, 2)) {
                task.setUnscheduledReason("No feasible theory slot under division/faculty/shift constraints");
            }
        }
    }

    private List<List<SchedulingTask>> buildLabRounds(List<SchedulingTask> divisionLabTasks) {
        Map<String, Deque<SchedulingTask>> byBatch = new LinkedHashMap<>();
        for (SchedulingTask task : divisionLabTasks) {
            String batch = normalizeBatch(task.getBatch());
            byBatch.computeIfAbsent(batch, key -> new ArrayDeque<>()).add(task);
        }

        List<List<SchedulingTask>> rounds = new ArrayList<>();
        boolean hasRemaining = true;
        while (hasRemaining) {
            hasRemaining = false;
            List<SchedulingTask> round = new ArrayList<>();
            for (Deque<SchedulingTask> queue : byBatch.values()) {
                if (!queue.isEmpty()) {
                    hasRemaining = true;
                    round.add(queue.pollFirst());
                }
            }
            if (!round.isEmpty()) {
                rounds.add(round);
            }
        }
        return rounds;
    }

    private boolean placeLabRound(String divisionKey, List<SchedulingTask> round) {
        DivisionScheduleMatrix divisionMatrix = divisionMatrices.get(divisionKey);
        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {
            if (isDivisionHoliday(round, day)) {
                continue;
            }

            for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY - 1; slot++) {
                if (crossesLunchBreak(slot)) {
                    continue;
                }
                if (!canPlaceLabRound(divisionMatrix, round, day, slot)) {
                    continue;
                }

                for (SchedulingTask task : round) {
                    assignLab(task, divisionKey, day, slot);
                }
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceLabRound(DivisionScheduleMatrix divisionMatrix,
                                     List<SchedulingTask> round,
                                     int day,
                                     int slot) {
        if (divisionMatrix.getDayLoad(day) + 2 > SlotConstants.MAX_DIVISION_LECTURES_PER_DAY) {
            return false;
        }

        Set<String> facultyCodesInRound = new HashSet<>();
        Set<String> roundSubjects = new HashSet<>();

        for (SchedulingTask task : round) {
            if (!facultyCodesInRound.add(task.getFacultyCode())) {
                return false;
            }

            if (!divisionMatrix.canAssign(task, day, slot) || !divisionMatrix.canAssign(task, day, slot + 1)) {
                return false;
            }

            FacultyScheduleMatrix facultyMatrix = facultyMatrices.get(task.getFacultyCode());
            if (!facultyMatrix.isFree(day, slot) || !facultyMatrix.isFree(day, slot + 1)) {
                return false;
            }
            if (!facultyMatrix.canTakeMore(day, 2)) {
                return false;
            }

            if (!isWithinShifts(task, slot) || !isWithinShifts(task, slot + 1)) {
                return false;
            }

            if (countLabSubjectForDay(divisionMatrix, task.getSubject(), day) > 0
                    && !roundSubjects.contains(task.getSubject())) {
                return false;
            }
            roundSubjects.add(task.getSubject());
        }
        return true;
    }

    private boolean tryPlaceTheory(SchedulingTask task, int maxPerDay) {
        String divisionKey = getDivisionKey(task);
        DivisionScheduleMatrix divisionMatrix = divisionMatrices.get(divisionKey);
        FacultyScheduleMatrix facultyMatrix = facultyMatrices.get(task.getFacultyCode());

        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {
            if (isDivisionHoliday(task, day)) {
                continue;
            }
            if (getSubjectCount(divisionKey, task.getSubject(), day) >= maxPerDay) {
                continue;
            }

            for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {
                if (!canPlaceTheory(task, divisionMatrix, facultyMatrix, day, slot, maxPerDay)) {
                    continue;
                }
                assignTheory(task, divisionKey, day, slot);
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceTheory(SchedulingTask task,
                                   DivisionScheduleMatrix divisionMatrix,
                                   FacultyScheduleMatrix facultyMatrix,
                                   int day,
                                   int slot,
                                   int maxPerDay) {
        if (!divisionMatrix.canAssign(task, day, slot)) {
            return false;
        }
        if (!facultyMatrix.isFree(day, slot)) {
            return false;
        }
        if (!facultyMatrix.canTakeMore(day, 1)) {
            return false;
        }
        if (divisionMatrix.getDayLoad(day) >= SlotConstants.MAX_DIVISION_LECTURES_PER_DAY) {
            return false;
        }
        if (!isWithinShifts(task, slot)) {
            return false;
        }
        return getSubjectCount(getDivisionKey(task), task.getSubject(), day) < maxPerDay;
    }

    private void assignLab(SchedulingTask task, String divisionKey, int day, int slot) {
        DivisionScheduleMatrix divisionMatrix = divisionMatrices.get(divisionKey);
        FacultyScheduleMatrix facultyMatrix = facultyMatrices.get(task.getFacultyCode());

        divisionMatrix.assign(day, slot, task);
        divisionMatrix.assign(day, slot + 1, task);
        facultyMatrix.assign(day, slot, task);
        facultyMatrix.assign(day, slot + 1, task);

        appendCell(divisionSchedule.get(divisionKey), day, slot, formatDivisionCell(task));
        appendCell(divisionSchedule.get(divisionKey), day, slot + 1, formatDivisionCell(task));
        facultySchedule.get(task.getFacultyCode())[day][slot] = formatFacultyCell(task);
        facultySchedule.get(task.getFacultyCode())[day][slot + 1] = formatFacultyCell(task);

        incrementSubjectCount(divisionKey, task.getSubject(), day);
        task.markAllocated();
    }

    private void assignTheory(SchedulingTask task, String divisionKey, int day, int slot) {
        divisionMatrices.get(divisionKey).assign(day, slot, task);
        facultyMatrices.get(task.getFacultyCode()).assign(day, slot, task);

        divisionSchedule.get(divisionKey)[day][slot] = formatDivisionCell(task);
        facultySchedule.get(task.getFacultyCode())[day][slot] = formatFacultyCell(task);

        incrementSubjectCount(divisionKey, task.getSubject(), day);
        task.markAllocated();
    }

    private int countLabSubjectForDay(DivisionScheduleMatrix matrix, String subject, int day) {
        int count = 0;
        for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {
            for (SchedulingTask existing : matrix.getTasks(day, slot)) {
                if (existing.getType() == TaskType.LAB && existing.getSubject().equals(subject)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int getSubjectCount(String divisionKey, String subject, int day) {
        Map<String, int[]> bySubject = subjectDailyCount.get(divisionKey);
        if (bySubject == null) {
            return 0;
        }
        int[] dayCounts = bySubject.get(subject);
        if (dayCounts == null || day >= dayCounts.length) {
            return 0;
        }
        return dayCounts[day];
    }

    private void incrementSubjectCount(String divisionKey, String subject, int day) {
        Map<String, int[]> bySubject = subjectDailyCount.computeIfAbsent(divisionKey, key -> new HashMap<>());
        int[] dayCounts = bySubject.computeIfAbsent(subject, key -> new int[SlotConstants.DAYS_PER_WEEK]);
        dayCounts[day] = dayCounts[day] + 1;
    }

    private boolean isDivisionHoliday(List<SchedulingTask> round, int dayIndex) {
        for (SchedulingTask task : round) {
            if (isDivisionHoliday(task, dayIndex)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDivisionHoliday(SchedulingTask task, int dayIndex) {
        Day holiday = task.getDivisionHoliday();
        return holiday != null && holiday.getIndex() == dayIndex;
    }

    private boolean isWithinShifts(SchedulingTask task, int slotIndex) {
        LocalTime[] slotRange = resolveSlotRange(task.getDivisionShift(), task.getFacultyShift(), slotIndex);
        if (slotRange == null) {
            return true;
        }

        ShiftWindow facultyWindow = parseShiftWindow(task.getFacultyShift());
        ShiftWindow divisionWindow = parseShiftWindow(task.getDivisionShift());

        boolean withinFaculty = facultyWindow == null || facultyWindow.contains(slotRange[0], slotRange[1]);
        boolean withinDivision = divisionWindow == null || divisionWindow.contains(slotRange[0], slotRange[1]);
        return withinFaculty && withinDivision;
    }

    private LocalTime[] resolveSlotRange(String divisionShift, String facultyShift, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= SlotConstants.SLOTS_PER_DAY) {
            return null;
        }
        String[] times;
        if (isMorningShift(divisionShift)) {
            times = SlotConstants.MORNING_TIMES;
        } else if (isAfternoonShift(divisionShift)) {
            times = SlotConstants.AFTERNOON_TIMES;
        } else if (isMorningShift(facultyShift)) {
            times = SlotConstants.MORNING_TIMES;
        } else {
            times = SlotConstants.AFTERNOON_TIMES;
        }

        String[] parts = times[slotIndex].split("-");
        if (parts.length != 2) {
            return null;
        }
        LocalTime start = parseTime(parts[0].trim());
        LocalTime end = parseTime(parts[1].trim());
        if (start == null || end == null) {
            return null;
        }
        return new LocalTime[]{start, end};
    }

    private ShiftWindow parseShiftWindow(String shiftText) {
        if (shiftText == null || shiftText.isBlank()) {
            return null;
        }
        String normalized = shiftText.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.contains("morning")) {
            return new ShiftWindow(LocalTime.of(8, 0), LocalTime.of(15, 0));
        }
        if (normalized.contains("afternoon")) {
            return new ShiftWindow(LocalTime.of(10, 0), LocalTime.of(17, 0));
        }

        String clean = shiftText.replace('\u2013', '-').replace('\u2014', '-').replace("to", "-");
        String[] parts = clean.split("-");
        if (parts.length != 2) {
            return null;
        }

        LocalTime start = parseFlexibleTime(parts[0].trim());
        LocalTime end = parseFlexibleTime(parts[1].trim());
        if (start == null || end == null) {
            return null;
        }
        return new ShiftWindow(start, end);
    }

    private LocalTime parseFlexibleTime(String text) {
        String value = text.replace('.', ':').replaceAll("\\s+", " ").trim().toUpperCase(Locale.ENGLISH);
        for (DateTimeFormatter formatter : SHIFT_FORMATTERS) {
            try {
                return LocalTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }
        return parseTime(value);
    }

    private LocalTime parseTime(String text) {
        String[] parts = text.trim().split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isMorningShift(String shift) {
        return shift != null && shift.trim().toLowerCase(Locale.ENGLISH).contains("morning");
    }

    private boolean isAfternoonShift(String shift) {
        return shift != null && shift.trim().toLowerCase(Locale.ENGLISH).contains("afternoon");
    }

    private boolean crossesLunchBreak(int slotStart) {
        return slotStart == SlotConstants.LUNCH_BREAK_AFTER_SLOT_INDEX;
    }

    private String normalizeBatch(String batch) {
        if (batch == null || batch.isBlank()) {
            return "__DEFAULT__";
        }
        return batch.trim().toUpperCase(Locale.ENGLISH);
    }

    private void appendCell(String[][] grid, int day, int slot, String value) {
        if (grid[day][slot] == null || grid[day][slot].isBlank()) {
            grid[day][slot] = value;
            return;
        }
        grid[day][slot] = grid[day][slot] + " | " + value;
    }

    private String formatDivisionCell(SchedulingTask task) {
        if (task.getType() == TaskType.LAB) {
            return task.getSubject() + " LAB [" + task.getBatch() + "] (" + task.getFacultyName() + ")";
        }
        return task.getSubject() + " (" + task.getFacultyName() + ")";
    }

    private String formatFacultyCell(SchedulingTask task) {
        return task.getSubject() + " (Sem" + task.getSemester() + " Div" + task.getDivision() + ")";
    }

    private void collectUnscheduled() {
        unscheduledTasks.clear();
        for (SchedulingTask task : tasks) {
            if (!task.isAllocated()) {
                if (task.getUnscheduledReason() == null || task.getUnscheduledReason().isBlank()) {
                    task.setUnscheduledReason("No feasible slot with current constraints");
                }
                unscheduledTasks.add(task);
            }
        }
    }

    private String getDivisionKey(SchedulingTask task) {
        return "Sem" + task.getSemester() + "_Div" + task.getDivision();
    }

    public List<SchedulingTask> getUnscheduledTasks() {
        return unscheduledTasks;
    }

    public Map<String, DivisionScheduleMatrix> getDivisionMatrices() {
        return divisionMatrices;
    }

    public Map<String, FacultyScheduleMatrix> getFacultyMatrices() {
        return facultyMatrices;
    }

    private static class ShiftWindow {
        private final LocalTime start;
        private final LocalTime end;

        private ShiftWindow(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        private boolean contains(LocalTime slotStart, LocalTime slotEnd) {
            return !slotStart.isBefore(start) && !slotEnd.isAfter(end);
        }
    }
}
