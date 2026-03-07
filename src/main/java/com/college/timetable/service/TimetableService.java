package com.college.timetable.service;

import com.college.timetable.scheduler.load.builder.LoadModelBuilder;
import com.college.timetable.scheduler.load.solver.LoadBacktrackingSolver;
import com.college.timetable.scheduler.load.core.LoadSchedulingResult;
import com.college.timetable.scheduler.load.core.SchedulingTask;

import com.college.timetable.scheduler.pipeline.SchedulingPipeline;
import com.college.timetable.scheduler.stage.LabSchedulingStage;
import com.college.timetable.scheduler.stage.TheorySchedulingStage;
import com.college.timetable.scheduler.stage.ValidationStage;

import com.college.timetable.model.TimetableSlot;
import com.college.timetable.config.SlotConfig;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TimetableService {

    // ===== Old Pipeline (Optional) =====
    private final SchedulingPipeline pipeline;
    private final LabSchedulingStage labStage;
    private final TheorySchedulingStage theoryStage;
    private final ValidationStage validationStage;
    private final SlotConfig slotConfig;

    public TimetableService(SchedulingPipeline pipeline,
                            LabSchedulingStage labStage,
                            TheorySchedulingStage theoryStage,
                            ValidationStage validationStage,
                            SlotConfig slotConfig) {
        this.pipeline = pipeline;
        this.labStage = labStage;
        this.theoryStage = theoryStage;
        this.validationStage = validationStage;
        this.slotConfig = slotConfig;
    }

    // ======================================================
    // NEW LOAD-BASED SCHEDULER (Backtracking Solver)
    // ======================================================

    public LoadSchedulingResult generateFromExcel(MultipartFile file) throws Exception {

        // 1️⃣ Build tasks
        LoadModelBuilder builder = new LoadModelBuilder();
        List<SchedulingTask> tasks = builder.buildTasks(file);

        // 2️⃣ Solve
        LoadBacktrackingSolver solver = new LoadBacktrackingSolver(tasks);
        solver.solve();

        LoadSchedulingResult result = new LoadSchedulingResult(
                solver.getDivisionMatrices(),
                solver.getFacultyMatrices(),
                solver.getUnscheduledTasks()
        );

        return result;
    }

    // ======================================================
    // OPTIONAL: Old Simple Pipeline Method
    // ======================================================

    public List<TimetableSlot> generateTimetable(TimetableContext context) {

        pipeline.reset();
        pipeline.addStage(labStage);
        pipeline.addStage(theoryStage);
        pipeline.addStage(validationStage);

        pipeline.execute(context);

        return context.getTimetableSlots();
    }
}
