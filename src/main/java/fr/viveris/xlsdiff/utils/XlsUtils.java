package fr.viveris.xlsdiff.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsUtils {

    private XlsUtils() {
        // Utility class, no constructor
    }

    public static String getStringCellValue(final Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return String.valueOf(cell.getDateCellValue());
            } else {
                final String value = String.valueOf(cell.getNumericCellValue());
                if (value.endsWith(".0")) {
                    return value.replaceAll("\\.0", "");
                } else {
                    return value;
                }

            }
        case Cell.CELL_TYPE_FORMULA:
            switch (cell.getCachedFormulaResultType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return String.valueOf(cell.getDateCellValue());
                } else {
                    final String value = String.valueOf(cell
                            .getNumericCellValue());
                    if (value.endsWith(".0")) {
                        return value.replaceAll("\\.0", "");
                    } else {
                        return value;
                    }
                }
            default:
                return cell.getStringCellValue();
            }
        case Cell.CELL_TYPE_BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        default:
            return cell.getStringCellValue();
        }
    }

    public static Workbook openFile(final String fileName)
            throws FileNotFoundException, IOException {
        if (fileName.endsWith(".xls")) {
            try (final InputStream stream = new FileInputStream(fileName)) {
                try (final HSSFWorkbook wk = new HSSFWorkbook(stream)) {
                    return wk;
                }
            }
        } else {
            try (final InputStream stream = new FileInputStream(fileName)) {
                try (final XSSFWorkbook wk = new XSSFWorkbook(stream)) {
                    return wk;
                }
            }
        }
    }

}
