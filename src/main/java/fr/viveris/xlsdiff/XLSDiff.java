package fr.viveris.xlsdiff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class XLSDiff {

    public static void main(final String[] args) {
        final XLSDiff xlsDiff = new XLSDiff();
        try {
            xlsDiff.performDiff(args[0], args[1]);
        } catch (final IOException e) {
            log.error("Main Error", e);
        }
    }

    public void performDiff(final String fileName1, final String fileName2)
            throws FileNotFoundException, IOException {
        if (fileName1.endsWith(".xls")) {
            try (final InputStream stream1 = new FileInputStream(fileName1)) {
                try (final InputStream stream2 = new FileInputStream(fileName2)) {
                    try (final HSSFWorkbook wk1 = new HSSFWorkbook(stream1)) {
                        try (final HSSFWorkbook wk2 = new HSSFWorkbook(stream2)) {
                            for (int sheetIndex = 0; sheetIndex < wk1
                                    .getNumberOfSheets(); ++sheetIndex) {
                                final Sheet sheet1 = wk1.getSheetAt(sheetIndex);
                                final Sheet sheet2 = wk2.getSheet(sheet1
                                        .getSheetName());
                                if (sheet2 == null) {
                                    log.error(
                                            "Sheet {} doesn't exist in {} workbook",
                                            sheet1.getSheetName(), fileName2);
                                    continue;
                                }
                                checkSheet(sheet1, sheet2);
                            }
                            for (int sheetIndex = 0; sheetIndex < wk2
                                    .getNumberOfSheets(); ++sheetIndex) {
                                final Sheet sheet2 = wk1.getSheetAt(sheetIndex);
                                final Sheet sheet1 = wk1.getSheet(sheet2
                                        .getSheetName());
                                if (sheet1 == null) {
                                    log.error(
                                            "Sheet {} doesn't exist in {} workbook",
                                            sheet2.getSheetName(), fileName1);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            try (final InputStream stream1 = new FileInputStream(fileName1)) {
                try (final InputStream stream2 = new FileInputStream(fileName2)) {
                    try (final XSSFWorkbook wk1 = new XSSFWorkbook(stream1)) {
                        try (final XSSFWorkbook wk2 = new XSSFWorkbook(stream2)) {
                            for (final Sheet sheet1 : wk1) {
                                final Sheet sheet2 = wk2.getSheet(sheet1
                                        .getSheetName());
                                if (sheet2 == null) {
                                    log.error(
                                            "Sheet {} doesn't exist in {} workbook",
                                            sheet1.getSheetName(), fileName1);
                                    continue;
                                }
                                checkSheet(sheet1, sheet2);
                            }
                            for (final Sheet sheet2 : wk2) {
                                final Sheet sheet1 = wk1.getSheet(sheet2
                                        .getSheetName());
                                if (sheet1 == null) {
                                    log.error(
                                            "Sheet {} doesn't exist in {} workbook",
                                            sheet2.getSheetName(), fileName1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkSheet(final Sheet sheet1, final Sheet sheet2) {
        log.info("Checking sheet named : {}", sheet1.getSheetName());
        for (final Row row1 : sheet1) {
            final int rowNum = row1.getRowNum();
            final Row row2 = sheet2.getRow(rowNum);
            for (final Cell cell1 : row1) {
                final int cellNum = cell1.getColumnIndex();
                final Cell cell2 = row2.getCell(cellNum);
                compareCell(cell1, cell2);
            }
        }
        for (final Row row2 : sheet2) {
            final int rowNum = row2.getRowNum();
            final Row row1 = sheet1.getRow(rowNum);
            for (final Cell cell2 : row2) {
                final int cellNum = cell2.getColumnIndex();
                final Cell cell1 = row1.getCell(cellNum);
                compareCell(cell1, cell2);
            }
        }

    }

    private void compareCell(final Cell cell1, final Cell cell2) {
        // TODO Auto-generated method stub

    }

}
