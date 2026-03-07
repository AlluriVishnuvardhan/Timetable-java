package com.college.timetable.excel;

import com.college.timetable.scheduler.load.core.SchedulingTask;
import com.college.timetable.scheduler.load.core.TaskType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class FacultyExcelExporter {

    private static final String[] DAY_NAMES = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public void export(String filePath, String title, SchedulingTask[][] matrix, String[] times) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Timetable");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle lectureStyle = createLectureStyle(workbook);
            CellStyle labStyle = createLabStyle(workbook, lectureStyle);

            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue(title);

            Row header = sheet.createRow(1);
            header.setHeightInPoints(40);
            createHeaderCell(header, 0, "Day", headerStyle);
            createHeaderCell(header, 1, times[0], headerStyle);
            createHeaderCell(header, 2, times[1], headerStyle);
            createHeaderCell(header, 3, times[2], headerStyle);
            createHeaderCell(header, 4, times[3], headerStyle);
            createHeaderCell(header, 5, times[4], headerStyle);
            createHeaderCell(header, 6, times[5], headerStyle);

            for (int row = 0; row < matrix.length; row++) {
                Row excelRow = sheet.createRow(row + 2);
                excelRow.setHeightInPoints(40);

                Cell dayCell = excelRow.createCell(0);
                dayCell.setCellValue(row < DAY_NAMES.length ? DAY_NAMES[row] : "Day " + (row + 1));
                dayCell.setCellStyle(headerStyle);

                for (int col = 0; col < matrix[row].length; col++) {
                    Cell cell = excelRow.createCell(col + 1);
                    SchedulingTask task = matrix[row][col];

                    if (task == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(lectureStyle);
                    } else {
                        cell.setCellValue(task.getSubject() + "\nSem" + task.getSemester() + " Div" + task.getDivision());
                        cell.setCellStyle(task.getType() == TaskType.LAB ? labStyle : lectureStyle);
                    }
                }
            }

            for (int i = 0; i < 10; i++) {
                sheet.setColumnWidth(i, 6000);
            }

            workbook.write(fos);
        }
    }

    private static void createHeaderCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        return headerStyle;
    }

    private static CellStyle createLectureStyle(Workbook workbook) {
        CellStyle lectureStyle = workbook.createCellStyle();
        lectureStyle.setAlignment(HorizontalAlignment.CENTER);
        lectureStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        lectureStyle.setWrapText(true);
        lectureStyle.setBorderBottom(BorderStyle.THIN);
        lectureStyle.setBorderTop(BorderStyle.THIN);
        lectureStyle.setBorderLeft(BorderStyle.THIN);
        lectureStyle.setBorderRight(BorderStyle.THIN);
        return lectureStyle;
    }

    private static CellStyle createLabStyle(Workbook workbook, CellStyle lectureStyle) {
        CellStyle labStyle = workbook.createCellStyle();
        labStyle.cloneStyleFrom(lectureStyle);
        labStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        labStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return labStyle;
    }
}
