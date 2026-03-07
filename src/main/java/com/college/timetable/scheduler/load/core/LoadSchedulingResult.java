package com.college.timetable.scheduler.load.core;

import java.util.List;
import java.util.Map;

/**
 * Final result returned after timetable scheduling completes.
 * 
 * Contains:
 * 1. Division schedule matrices
 * 2. Faculty schedule matrices
 * 3. List of tasks that could not be scheduled
 */
public class LoadSchedulingResult {

    // Division -> Timetable Matrix
    private final Map<String, DivisionScheduleMatrix> divisionMatrices;

    // Faculty -> Timetable Matrix
    private final Map<String, FacultyScheduleMatrix> facultyMatrices;

    // Tasks that could not be scheduled
    private final List<SchedulingTask> unscheduledTasks;

    public LoadSchedulingResult(
            Map<String, DivisionScheduleMatrix> divisionMatrices,
            Map<String, FacultyScheduleMatrix> facultyMatrices,
            List<SchedulingTask> unscheduledTasks) {

        this.divisionMatrices = divisionMatrices;
        this.facultyMatrices = facultyMatrices;
        this.unscheduledTasks = unscheduledTasks;
    }

    /**
     * Get division timetable matrices
     */
    public Map<String, DivisionScheduleMatrix> getDivisionMatrices() {
        return divisionMatrices;
    }

    /**
     * Get faculty timetable matrices
     */
    public Map<String, FacultyScheduleMatrix> getFacultyMatrices() {
        return facultyMatrices;
    }

    /**
     * Get unscheduled tasks
     */
    public List<SchedulingTask> getUnscheduledTasks() {
        return unscheduledTasks;
    }

    /**
     * Utility: Check if all tasks were scheduled
     */
    public boolean isFullyScheduled() {
        return unscheduledTasks == null || unscheduledTasks.isEmpty();
    }

    /**
     * Utility: Get number of unscheduled tasks
     */
    public int getUnscheduledCount() {
        return unscheduledTasks == null ? 0 : unscheduledTasks.size();
    }
}