package top.felixu.platform.util.excel;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author felixu
 * @since 2020.11.04
 */
public class ExcelReader {

    @SafeVarargs
    public static <T> List<T> readFromExcel(InputStream inputStream, int sheetIndex, int skip, Supplier<T> dataSupplier, Consumer<ExcelCellReader<T>>... mappers) throws IOException {
        XSSFWorkbook wb;
        try {
            wb = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new InternalException("不是一个正确的xlsx文件");
        }
        XSSFSheet sheet;
        try {
            sheet = wb.getSheetAt(sheetIndex);
        } catch (IllegalArgumentException e) {
            throw new InternalException("缺少第" + (sheetIndex + 1) + "个sheet");
        }
        List<T> result = readFormSheet(sheet, skip, dataSupplier, mappers);
        wb.close();
        return result;
    }

    @SafeVarargs
    public static <T> List<T> readFormSheet(Sheet sheet, int skip, Supplier<T> dataSupplier, Consumer<ExcelCellReader<T>>... mappers) {
        String sheetName = sheet.getSheetName();
        List<T> result = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < skip) {
                // 跳过需要忽略的行数
                continue;
            }
            T t = dataSupplier.get();
            result.add(t);
            for (int i = 0; i < mappers.length; i++) {
                Consumer<ExcelCellReader<T>> mapper = mappers[i];
                if (mapper != null) {
                    mapper.accept(new ExcelCellReader<>(sheetName, row.getCell(i), row.getRowNum() + 1, i + 1, t));
                }
            }
        }
        return result;
    }

    @SafeVarargs
    public static <T> List<T> readFromExcel(InputStream inputStream, Supplier<T> dataSupplier, Consumer<ExcelCellReader<T>>... mappers) throws IOException {
        return readFromExcel(inputStream, 0, 1, dataSupplier, mappers);
    }
}
