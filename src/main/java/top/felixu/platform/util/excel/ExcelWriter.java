package top.felixu.platform.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author felixu
 * @since 2020.11.04
 */
public class ExcelWriter {

    private static final String EXCEL_SUFFIX = ".xlsx";

    @SafeVarargs
    public static <T> void createSheet(Workbook wb, String sheetName, List<T> data, ExcelColumnWriter<T, Object>... columns) {
        // excel中的sheet
        Sheet sheet = wb.createSheet(sheetName);
        // 标题行
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            titleRow.createCell(i).setCellValue(columns[i].getTitle());
        }
        // 开始填充数据
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columns.length; j++) {
                Object value = columns[j].getMapper().apply(data.get(i));
                Cell cell = row.createCell(j);
                if (value == null || value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof RichTextString) {
                    cell.setCellValue((RichTextString) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof Calendar) {
                    cell.setCellValue((Calendar) value);
                } else if (value instanceof LocalDate) {
                    cell.setCellValue(((LocalDate) value).toString());
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }
        // 设置自动宽度
//        for (int i = 0; i < columns.length; i++) {
//            sheet.autoSizeColumn(i); // 已知在 docker 镜像 openjdk8-alpine 中这行会报错！
//        }
    }

    public static void export(Workbook workbook, String fileName, HttpServletResponse response) throws IOException {
        if (!fileName.endsWith(EXCEL_SUFFIX)) {
            fileName = fileName + EXCEL_SUFFIX;
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @SafeVarargs
    public static <T> void createAndExport(HttpServletResponse response, String sheetName, List<T> data, ExcelColumnWriter<T, Object>... columns) throws IOException {
        Workbook workbook = create(sheetName, data, columns);
        export(workbook, sheetName + EXCEL_SUFFIX, response);
    }

    @SafeVarargs
    public static <T> SXSSFWorkbook create(String sheetName, List<T> data, ExcelColumnWriter<T, Object>... columns) {
        // excel文件对象
        SXSSFWorkbook wb = new SXSSFWorkbook();
        createSheet(wb, sheetName, data, columns);
        return wb;
    }

    public static void export(Workbook workbook, OutputStream out) throws IOException {
        workbook.write(out);
        workbook.close();
    }

    @SafeVarargs
    public static <T> void createAndExport(OutputStream out, String sheetName, List<T> data, ExcelColumnWriter<T, Object>... columns) throws IOException {
        Workbook workbook = create(sheetName, data, columns);
        export(workbook, out);
    }
}
