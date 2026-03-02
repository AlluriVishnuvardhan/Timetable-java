package com.college.timetable.scheduler.load.builder;

import com.college.timetable.scheduler.load.core.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

public class LoadModelBuilder {

    public List<SchedulingTask> buildTasks(MultipartFile file) throws Exception {

        List<SchedulingTask> tasks = new ArrayList<>();

        InputStream is = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);

        String currentFacultyCode = null;
        String currentFacultyName = null;
        String currentFacultyShift = null;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            // Faculty columns
            Cell nameCell = row.getCell(0);
            Cell fullNameCell = row.getCell(1);
            Cell shiftCell = row.getCell(3);

            if (nameCell != null && !nameCell.toString().isBlank()) {
                currentFacultyCode = nameCell.toString().trim();
                currentFacultyName = fullNameCell.toString().trim();
                currentFacultyShift = shiftCell.toString().trim();
            }

            if (currentFacultyCode == null) continue;

            String subject = getString(row.getCell(5));
            String type = getString(row.getCell(6));
            String semester = getString(row.getCell(7));
            String division = getString(row.getCell(8));
            String divShift = getString(row.getCell(9));
            String theoryClassesStr = getString(row.getCell(11));
            String numLabsStr = getString(row.getCell(12));
            String holidayStr = getString(row.getCell(15));

            if (subject.isBlank() || division.isBlank()) continue;

            String year = convertSemesterToYear(semester);
            Day divisionHoliday = parseHoliday(holidayStr);

            if ("Theory".equalsIgnoreCase(type)) {

                int theoryCount = parseInt(theoryClassesStr);

                for (int t = 0; t < theoryCount; t++) {
                    tasks.add(new SchedulingTask(
                            currentFacultyCode,
                            currentFacultyName,
                            year,
                            division,
                            subject,
                            TaskType.THEORY,
                            false,
                            currentFacultyShift,
                            divShift,
                            divisionHoliday
                    ));
                }
            }

            if ("Lab".equalsIgnoreCase(type)) {

                int labCount = parseInt(numLabsStr);

                for (int l = 0; l < labCount; l++) {
                    tasks.add(new SchedulingTask(
                            currentFacultyCode,
                            currentFacultyName,
                            year,
                            division,
                            subject,
                            TaskType.LAB,
                            true,
                            currentFacultyShift,
                            divShift,
                            divisionHoliday
                    ));
                }
            }
        }

        workbook.close();

        sortTasks(tasks);

        return tasks;
    }

    private void sortTasks(List<SchedulingTask> tasks) {

        tasks.sort((a, b) -> {
            if (a.getType() != b.getType()) {
                return a.getType() == TaskType.LAB ? -1 : 1;
            }
            return 0;
        });
    }

    private String getString(Cell cell) {
        if (cell == null) return "";
        return cell.toString().trim();
    }

    private int parseInt(String value) {
        try {
            return (int) Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private Day parseHoliday(String holiday) {
        if (holiday == null) return null;

        switch (holiday.trim().toUpperCase()) {
            case "MON":
            case "MONDAY":
                return Day.MONDAY;
            case "SAT":
            case "SATURDAY":
                return Day.SATURDAY;
            default:
                return null;
        }
    }

    private String convertSemesterToYear(String semester) {

        if (semester == null) return "";

        switch (semester.trim()) {
            case "3":
            case "4":
                return "2nd Year";
            case "5":
            case "6":
                return "3rd Year";
            case "7":
            case "8":
                return "4th Year";
            default:
                return "Unknown";
        }
    }
}