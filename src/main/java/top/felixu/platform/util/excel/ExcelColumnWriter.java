package top.felixu.platform.util.excel;

import lombok.Data;

import java.util.function.Function;

/**
 * @author felixu
 * @since 2020.11.04
 */
@Data
public class ExcelColumnWriter<T, R> {
    /**
     * 列标题
     */
    private final String title;
    /**
     * 如何获取单元格内容
     */
    private final Function<? super T, R> mapper;

}
