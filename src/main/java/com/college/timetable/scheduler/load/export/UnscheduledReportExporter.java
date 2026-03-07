package com.college.timetable.scheduler.load.export;

import com.college.timetable.scheduler.load.core.SchedulingTask;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class UnscheduledReportExporter {

    public void export(List<SchedulingTask> unscheduledTasks, String filePath) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Unscheduled Report");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Faculty");
        header.createCell(1).setCellValue("Subject");
        header.createCell(2).setCellValue("Division");
        header.createCell(3).setCellValue("Reason");

        int rowIndex = 1;

        for (SchedulingTask task : unscheduledTasks) {

            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(task.getFacultyName());
            row.createCell(1).setCellValue(task.getSubject());
            row.createCell(2).setCellValue(task.getDivision());
            row.createCell(3).setCellValue(
                    task.getUnscheduledReason() == null ? "Constraint conflict" : task.getUnscheduledReason()
            );
        }

        for (int i = 0; i <= 3; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);

        fos.close();
        workbook.close();
    }
}
