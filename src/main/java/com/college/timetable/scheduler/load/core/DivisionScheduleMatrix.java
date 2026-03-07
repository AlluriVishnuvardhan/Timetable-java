package com.college.timetable.scheduler.load.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DivisionScheduleMatrix {

    private final List<SchedulingTask>[][] matrix;

    @SuppressWarnings("unchecked")
    public DivisionScheduleMatrix() {
        matrix = new ArrayList[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY];
        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {
            for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {
                matrix[day][slot] = new ArrayList<>();
            }
        }
    }

    public boolean isFree(int day, int slot) {
        return matrix[day][slot].isEmpty();
    }

    public boolean canAssign(SchedulingTask task, int day, int slot) {
        List<SchedulingTask> cell = matrix[day][slot];
        if (cell.isEmpty()) {
            return true;
        }

        // Parallel entries are only allowed for LAB batches in the same division slot.
        if (task.getType() != TaskType.LAB) {
            return false;
        }

        for (SchedulingTask existing : cell) {
            if (existing.getType() != TaskType.LAB) {
                return false;
            }
            String existingBatch = existing.getBatch();
            String taskBatch = task.getBatch();
            if (existingBatch == null || taskBatch == null
                    || existingBatch.isBlank() || taskBatch.isBlank()) {
                return false;
            }
            if (existingBatch.equalsIgnoreCase(taskBatch)) {
                return false;
            }
        }
        return true;
    }

    public int getDayLoad(int day) {
        int count = 0;
        for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {
            if (!matrix[day][slot].isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public boolean subjectAlreadyScheduledToday(String subject, int day) {
        for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {
            for (SchedulingTask task : matrix[day][slot]) {
                if (task.getSubject().equals(subject)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void assign(int day, int slot, SchedulingTask task) {
        matrix[day][slot].add(task);
    }

    public void unassign(int day, int slot, SchedulingTask task) {
        matrix[day][slot].remove(task);
    }

    public SchedulingTask getTask(int day, int slot) {
        if (matrix[day][slot].isEmpty()) {
            return null;
        }
        return matrix[day][slot].get(0);
    }

    public List<SchedulingTask> getTasks(int day, int slot) {
        return matrix[day][slot];
    }

    public SchedulingTask[][] getMatrix() {
        SchedulingTask[][] primary = new SchedulingTask[SlotConstants.DAYS_PER_WEEK][SlotConstants.SLOTS_PER_DAY];
        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {
            for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {
                primary[day][slot] = getTask(day, slot);
            }
        }
        return primary;
    }

    public int getRows() {
        return matrix.length;
    }

    public int getCols() {
        return matrix[0].length;
    }

    public String getCell(int row, int col) {
        List<SchedulingTask> cell = matrix[row][col];
        if (cell.isEmpty()) {
            return "";
        }
        return cell.stream()
                .map(this::formatTask)
                .collect(Collectors.joining(" | "));
    }

    private String formatTask(SchedulingTask task) {
        if (task.getType() == TaskType.LAB && task.getBatch() != null && !task.getBatch().isBlank()) {
            return task.getSubject() + " LAB [" + task.getBatch() + "] (" + task.getFacultyName() + ")";
        }
        return task.getSubject() + " (" + task.getFacultyName() + ")";
    }
}
