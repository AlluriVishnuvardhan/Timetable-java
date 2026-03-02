package com.college.timetable.scheduler.load.solver;

import com.college.timetable.scheduler.load.core.*;

import java.util.*;

public class LoadBacktrackingSolver {

    private final List<SchedulingTask> tasks;

    private final Map<String, DivisionScheduleMatrix> divisionMatrices = new HashMap<>();
    private final Map<String, FacultyScheduleMatrix> facultyMatrices = new HashMap<>();

    private final List<SchedulingTask> unscheduledTasks = new ArrayList<>();

    public LoadBacktrackingSolver(List<SchedulingTask> tasks) {
        this.tasks = tasks;
    }

    public void solve() {
        solveRecursive(0);
    }

    private boolean solveRecursive(int index) {

        if (index >= tasks.size()) {
            return true;
        }

        SchedulingTask task = tasks.get(index);

        boolean placed = tryPlaceTask(task);

        if (!placed) {
            unscheduledTasks.add(task);
        }

        return solveRecursive(index + 1);
    }

    private boolean tryPlaceTask(SchedulingTask task) {

        DivisionScheduleMatrix divisionMatrix =
                divisionMatrices.computeIfAbsent(
                        task.getDivision(),
                        d -> new DivisionScheduleMatrix()
                );

        FacultyScheduleMatrix facultyMatrix =
                facultyMatrices.computeIfAbsent(
                        task.getFacultyCode(),
                        f -> new FacultyScheduleMatrix()
                );

        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {

            if (isDivisionHoliday(task, day)) continue;

            for (int slot = 0; slot < SlotConstants.SLOTS_PER_DAY; slot++) {

                if (task.isRequiresContinuous()) {

                    if (slot >= SlotConstants.SLOTS_PER_DAY - 1) continue;

                    if (canAssignLab(divisionMatrix, facultyMatrix, day, slot)) {

                        assignLab(task, divisionMatrix, facultyMatrix, day, slot);
                        return true;
                    }

                } else {

                    if (canAssignTheory(divisionMatrix, facultyMatrix, day, slot)) {

                        assignTheory(task, divisionMatrix, facultyMatrix, day, slot);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isDivisionHoliday(SchedulingTask task, int dayIndex) {

        if (task.getDivisionHoliday() == null) return false;

        return task.getDivisionHoliday().getIndex() == dayIndex;
    }

    private boolean canAssignTheory(DivisionScheduleMatrix divisionMatrix,
                                    FacultyScheduleMatrix facultyMatrix,
                                    int day,
                                    int slot) {

        if (!divisionMatrix.isFree(day, slot)) return false;

        if (!facultyMatrix.isFree(day, slot)) return false;

        return true;
    }

    private boolean canAssignLab(DivisionScheduleMatrix divisionMatrix,
                                 FacultyScheduleMatrix facultyMatrix,
                                 int day,
                                 int slot) {

        if (!divisionMatrix.isFree(day, slot)) return false;
        if (!divisionMatrix.isFree(day, slot + 1)) return false;

        if (!facultyMatrix.isFree(day, slot)) return false;
        if (!facultyMatrix.isFree(day, slot + 1)) return false;

        return true;
    }

    private void assignTheory(SchedulingTask task,
                              DivisionScheduleMatrix divisionMatrix,
                              FacultyScheduleMatrix facultyMatrix,
                              int day,
                              int slot) {

        divisionMatrix.assign(day, slot, null);
        facultyMatrix.assign(day, slot);
        task.markAllocated();
    }

    private void assignLab(SchedulingTask task,
                           DivisionScheduleMatrix divisionMatrix,
                           FacultyScheduleMatrix facultyMatrix,
                           int day,
                           int slot) {

        divisionMatrix.assign(day, slot, null);
        divisionMatrix.assign(day, slot + 1, null);

        facultyMatrix.assign(day, slot);
        facultyMatrix.assign(day, slot + 1);

        task.markAllocated();
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
}