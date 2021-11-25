package top.felixu.platform.util.excel;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author : zhan9yn
 * @version : 1.0
 * @date : 2021/11/24 3:49 下午
 */
public class ExcelReader {
    @SafeVarargs
    /**
     *
     * @param inputStream file 文件留
     * @param sheetIndex sheet 索引， from 0
     * @param start 从 start 行开始， from 1
     * @param dataSupplier 需要转换的类型
     * @param mappers 每个cell转换的过程
     * @return java.util.List<T>
     * @throws
     * @author zhan9yn
     * @date 2021/11/24 5:13 下午
    */
    public static <T> List<T> readFromExcel(InputStream inputStream, int sheetIndex, int start, Supplier<T> dataSupplier, Consumer<ExcelCellReader<T>>... mappers) throws IOException {
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
        String sheetName = sheet.getSheetName();
        List<T> result = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < start) {
                // 跳过标题行
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
        wb.close();
        return result;
    }

    @SafeVarargs
    public static <T> List<T> readFromExcel(InputStream inputStream, int start, Supplier<T> dataSupplier, Consumer<ExcelCellReader<T>>... mappers) throws IOException {
        return readFromExcel(inputStream, 0, 1, dataSupplier, mappers);
    }
}
