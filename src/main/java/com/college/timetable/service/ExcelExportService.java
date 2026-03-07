package com.college.timetable.service;

import com.college.timetable.scheduler.load.core.DivisionScheduleMatrix;
import com.college.timetable.scheduler.load.core.FacultyScheduleMatrix;
import com.college.timetable.scheduler.load.core.SchedulingTask;
import com.college.timetable.scheduler.load.core.SlotConstants;
import com.college.timetable.scheduler.load.core.TaskType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class ExcelExportService {

    private static final String OUTPUT_ROOT = "outputs";
    private static final String[] DAY_NAMES = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

    public ExcelExportService() {
        new File(OUTPUT_ROOT).mkdirs();
    }

    public void exportDivisionTimetable(Map<String, DivisionScheduleMatrix> divisionMatrices) {
        Map<String, List<Map.Entry<String, DivisionScheduleMatrix>>> grouped = new LinkedHashMap<>();
        grouped.put("2nd Year", new ArrayList<>());
        grouped.put("3rd Year", new ArrayList<>());
        grouped.put("4th Year", new ArrayList<>());

        for (Map.Entry<String, DivisionScheduleMatrix> entry : divisionMatrices.entrySet()) {
            grouped.computeIfAbsent(resolveYear(entry.getKey()), k -> new ArrayList<>()).add(entry);
        }

        writeYearWorkbook(grouped.getOrDefault("2nd Year", List.of()), OUTPUT_ROOT + "/2nd_Year_Timetable.xlsx", "2nd Year Timetable");
        writeYearWorkbook(grouped.getOrDefault("3rd Year", List.of()), OUTPUT_ROOT + "/3rd_Year_Timetable.xlsx", "3rd Year Timetable");
        writeYearWorkbook(grouped.getOrDefault("4th Year", List.of()), OUTPUT_ROOT + "/4th_Year_Timetable.xlsx", "4th Year Timetable");
    }

    public void exportFacultyTimetable(Map<String, FacultyScheduleMatrix> facultyMatrices) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(OUTPUT_ROOT + "/Faculty_Timetable.xlsx")) {

            Styles styles = new Styles(workbook);
            List<String> facultyNames = new ArrayList<>(facultyMatrices.keySet());
            Collections.sort(facultyNames);

            for (String facultyName : facultyNames) {
                FacultyScheduleMatrix matrix = facultyMatrices.get(facultyName);
                String[] times = resolveTimes(matrix.getMatrix(), false);
                String sheetName = safeSheetName(facultyName);
                Sheet sheet = workbook.createSheet(sheetName);

                buildFacultySheet(sheet, styles, "Faculty: " + facultyName, matrix, times);
            }

            if (facultyNames.isEmpty()) {
                Sheet sheet = workbook.createSheet("Faculty");
                buildEmptySheet(sheet, styles, "Faculty Timetable", SlotConstants.MORNING_TIMES);
            }

            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportUnscheduled(List<SchedulingTask> tasks) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(OUTPUT_ROOT + "/unscheduled.xlsx")) {

            Sheet sheet = workbook.createSheet("Unscheduled");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle bodyStyle = createBodyStyle(workbook, null);

            Row header = sheet.createRow(0);
            createCell(header, 0, "Faculty", headerStyle);
            createCell(header, 1, "Subject", headerStyle);
            createCell(header, 2, "Division", headerStyle);
            createCell(header, 3, "Reason", headerStyle);

            int rowIndex = 1;
            for (SchedulingTask task : tasks) {
                Row row = sheet.createRow(rowIndex++);
                createCell(row, 0, task.getFacultyName(), bodyStyle);
                createCell(row, 1, task.getSubject(), bodyStyle);
                createCell(row, 2, "Sem" + task.getSemester() + " Div" + task.getDivision(), bodyStyle);
                createCell(row, 3, task.getUnscheduledReason() == null ? "Constraint conflict" : task.getUnscheduledReason(), bodyStyle);
            }

            sheet.setColumnWidth(0, 7000);
            sheet.setColumnWidth(1, 7000);
            sheet.setColumnWidth(2, 5500);
            sheet.setColumnWidth(3, 15000);

            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeYearWorkbook(List<Map.Entry<String, DivisionScheduleMatrix>> entries,
                                   String filePath,
                                   String title) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Styles styles = new Styles(workbook);

            if (entries.isEmpty()) {
                Sheet sheet = workbook.createSheet("Timetable");
                buildEmptySheet(sheet, styles, title, SlotConstants.MORNING_TIMES);
            } else {
                entries.sort(Comparator.comparing(Map.Entry::getKey));
                for (Map.Entry<String, DivisionScheduleMatrix> entry : entries) {
                    String divisionKey = entry.getKey();
                    DivisionScheduleMatrix matrix = entry.getValue();
                    String[] times = resolveTimes(matrix.getMatrix(), true);
                    Sheet sheet = workbook.createSheet(safeSheetName(divisionKey));
                    buildDivisionSheet(sheet, styles, title, divisionKey, matrix, times);
                }
            }

            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildDivisionSheet(Sheet sheet,
                                    Styles styles,
                                    String title,
                                    String divisionKey,
                                    DivisionScheduleMatrix matrix,
                                    String[] times) {

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

        Row row0 = sheet.createRow(0);
        createCell(row0, 0, "Sandip University - SOCSE", styles.titleStyle);

        Row row1 = sheet.createRow(1);
        createCell(row1, 0, title + " | " + divisionKey, styles.subTitleStyle);

        Row header = sheet.createRow(2);
        createCell(header, 0, "Day", styles.headerStyle);
        createCell(header, 1, times[0], styles.headerStyle);
        createCell(header, 2, times[1], styles.headerStyle);
        createCell(header, 3, "Lunch", styles.headerStyle);
        createCell(header, 4, times[2], styles.headerStyle);
        createCell(header, 5, times[3], styles.headerStyle);
        createCell(header, 6, times[4], styles.headerStyle);
        createCell(header, 7, times[5], styles.headerStyle);

        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {
            Row row = sheet.createRow(day + 3);
            createCell(row, 0, DAY_NAMES[day], styles.dayStyle);
            fillDivisionSlot(row, 1, matrix.getTasks(day, 0), styles);
            fillDivisionSlot(row, 2, matrix.getTasks(day, 1), styles);
            createCell(row, 3, "Lunch", styles.lunchStyle);
            fillDivisionSlot(row, 4, matrix.getTasks(day, 2), styles);
            fillDivisionSlot(row, 5, matrix.getTasks(day, 3), styles);
            fillDivisionSlot(row, 6, matrix.getTasks(day, 4), styles);
            fillDivisionSlot(row, 7, matrix.getTasks(day, 5), styles);
            row.setHeightInPoints(42);
        }

        applyColumnWidths(sheet);
    }

    private void buildFacultySheet(Sheet sheet,
                                   Styles styles,
                                   String title,
                                   FacultyScheduleMatrix matrix,
                                   String[] times) {

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        Row titleRow = sheet.createRow(0);
        createCell(titleRow, 0, title, styles.titleStyle);

        Row header = sheet.createRow(1);
        createCell(header, 0, "Day", styles.headerStyle);
        createCell(header, 1, times[0], styles.headerStyle);
        createCell(header, 2, times[1], styles.headerStyle);
        createCell(header, 3, "Lunch", styles.headerStyle);
        createCell(header, 4, times[2], styles.headerStyle);
        createCell(header, 5, times[3], styles.headerStyle);
        createCell(header, 6, times[4], styles.headerStyle);
        createCell(header, 7, times[5], styles.headerStyle);

        SchedulingTask[][] taskMatrix = matrix.getMatrix();
        for (int day = 0; day < SlotConstants.DAYS_PER_WEEK; day++) {
            Row row = sheet.createRow(day + 2);
            createCell(row, 0, DAY_NAMES[day], styles.dayStyle);
            fillFacultySlot(row, 1, taskMatrix[day][0], styles);
            fillFacultySlot(row, 2, taskMatrix[day][1], styles);
            createCell(row, 3, "Lunch", styles.lunchStyle);
            fillFacultySlot(row, 4, taskMatrix[day][2], styles);
            fillFacultySlot(row, 5, taskMatrix[day][3], styles);
            fillFacultySlot(row, 6, taskMatrix[day][4], styles);
            fillFacultySlot(row, 7, taskMatrix[day][5], styles);
            row.setHeightInPoints(42);
        }

        applyColumnWidths(sheet);
    }

    private void buildEmptySheet(Sheet sheet, Styles styles, String title, String[] times) {
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        Row titleRow = sheet.createRow(0);
        createCell(titleRow, 0, title, styles.titleStyle);

        Row header = sheet.createRow(1);
        createCell(header, 0, "Day", styles.headerStyle);
        createCell(header, 1, times[0], styles.headerStyle);
        createCell(header, 2, times[1], styles.headerStyle);
        createCell(header, 3, "Lunch", styles.headerStyle);
        createCell(header, 4, times[2], styles.headerStyle);
        createCell(header, 5, times[3], styles.headerStyle);
        createCell(header, 6, times[4], styles.headerStyle);
        createCell(header, 7, times[5], styles.headerStyle);

        applyColumnWidths(sheet);
    }

    private void fillDivisionSlot(Row row, int col, List<SchedulingTask> tasks, Styles styles) {
        if (tasks == null || tasks.isEmpty()) {
            createCell(row, col, "", styles.theoryStyle);
            return;
        }

        boolean onlyLab = true;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            SchedulingTask task = tasks.get(i);
            if (task.getType() != TaskType.LAB) {
                onlyLab = false;
            }
            if (i > 0) {
                sb.append("\n");
            }
            if (task.getType() == TaskType.LAB) {
                sb.append(task.getSubject())
                        .append(" [")
                        .append(task.getBatch() == null ? "" : task.getBatch())
                        .append("]")
                        .append(" (")
                        .append(task.getFacultyName())
                        .append(")");
            } else {
                sb.append(task.getSubject())
                        .append(" (")
                        .append(task.getFacultyName())
                        .append(")");
            }
        }

        createCell(row, col, sb.toString(), onlyLab ? styles.labStyle : styles.theoryStyle);
    }

    private void fillFacultySlot(Row row, int col, SchedulingTask task, Styles styles) {
        if (task == null) {
            createCell(row, col, "", styles.theoryStyle);
            return;
        }

        String value = task.getSubject() + "\nSem" + task.getSemester() + " Div" + task.getDivision();
        createCell(row, col, value, task.getType() == TaskType.LAB ? styles.labStyle : styles.theoryStyle);
    }

    private void applyColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 4200);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 3600);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);
        sheet.setColumnWidth(7, 5200);
    }

    private String[] resolveTimes(SchedulingTask[][] matrix, boolean division) {
        String shift = "";
        for (SchedulingTask[] row : matrix) {
            for (SchedulingTask task : row) {
                if (task == null) {
                    continue;
                }
                shift = division ? task.getDivisionShift() : task.getFacultyShift();
                if (shift != null && !shift.isBlank()) {
                    break;
                }
            }
            if (shift != null && !shift.isBlank()) {
                break;
            }
        }

        if (shift != null && shift.toLowerCase(Locale.ENGLISH).contains("morning")) {
            return SlotConstants.MORNING_TIMES;
        }
        return SlotConstants.AFTERNOON_TIMES;
    }

    private String resolveYear(String divisionKey) {
        String semester = extractSemester(divisionKey);
        switch (semester) {
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

    private String extractSemester(String divisionKey) {
        if (divisionKey == null || divisionKey.isBlank()) {
            return "";
        }
        String key = divisionKey.trim();
        if (!key.startsWith("Sem")) {
            return "";
        }
        int underscore = key.indexOf('_');
        if (underscore < 0) {
            return key.substring(3);
        }
        return key.substring(3, underscore);
    }

    private String safeSheetName(String name) {
        String sanitized = name.replaceAll("[\\\\/*?:\\[\\]]", "_");
        if (sanitized.length() > 31) {
            return sanitized.substring(0, 31);
        }
        if (sanitized.isBlank()) {
            return "Sheet";
        }
        return sanitized;
    }

    private static void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = createBodyStyle(workbook, IndexedColors.GREY_25_PERCENT.getIndex());
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createBodyStyle(Workbook workbook, Short color) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        if (color != null) {
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    private static class Styles {
        private final CellStyle titleStyle;
        private final CellStyle subTitleStyle;
        private final CellStyle headerStyle;
        private final CellStyle dayStyle;
        private final CellStyle theoryStyle;
        private final CellStyle labStyle;
        private final CellStyle lunchStyle;

        private Styles(Workbook workbook) {
            titleStyle = createTitleStyle(workbook, 13);
            subTitleStyle = createTitleStyle(workbook, 11);
            headerStyle = createFilledStyle(workbook, IndexedColors.GREY_25_PERCENT.getIndex(), true);
            dayStyle = createFilledStyle(workbook, IndexedColors.GREY_25_PERCENT.getIndex(), true);
            theoryStyle = createFilledStyle(workbook, IndexedColors.PALE_BLUE.getIndex(), false);
            labStyle = createFilledStyle(workbook, IndexedColors.LIGHT_YELLOW.getIndex(), false);
            lunchStyle = createFilledStyle(workbook, IndexedColors.GREY_40_PERCENT.getIndex(), true);
        }

        private static CellStyle createTitleStyle(Workbook workbook, int fontSize) {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) fontSize);
            style.setFont(font);
            return style;
        }

        private static CellStyle createFilledStyle(Workbook workbook, short color, boolean bold) {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setBold(bold);
            style.setFont(font);
            return style;
        }
    }
}
