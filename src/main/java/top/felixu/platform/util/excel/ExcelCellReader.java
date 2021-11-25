package top.felixu.platform.util.excel;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * <p>
 *     excel 的单元格（ cell ）取值
 * </p>
 * @author : zhan9yn
 * @version : 1.0
 * @date : 2021/11/24 3:21 下午
 */
@Data
public class ExcelCellReader<T> {

    private static final DateTimeFormatter SLASH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d");

    private final String sheetName;

    private final Cell cell;
    /**
     * 行号，用于抛出异常时指明所在行
     */
    private final int rowIndex;
    /**
     * 列号，用于抛出异常时指明所在列
     */
    private final int columnIndex;

    private final T data;


    public String getRequiredString() {
        String value = getNullableString();
        if (value == null) {
            throw new InternalException("缺少必填字段，sheet：" + sheetName + "，行：" + rowIndex + "，列：" + columnIndex + "。");
        }
        return value;
    }

    public String getDefaultString (String defaultStr) {
        String value = getNullableString();
        if (StringUtils.isEmpty(value)) {
            value = defaultStr;
        }
        return value;
    }

    public LocalDate getNullableDate() {
        String value = getNullableString();
        return value == null ? null : parseLocalDate(value);
    }

    public Long getNullableLong() {
        String value = this.getNullableString();
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value).longValue();
        } catch (NumberFormatException e) {
            throw new InternalException("整数格式错误：" + value + "，sheet：" + sheetName + "，行：" + (rowIndex) + "，列：" + (columnIndex));
        }
    }

    public Long getRequiredLong() {
        String value = getRequiredString();
        try {
            return new BigDecimal(value).longValue();
        } catch (NumberFormatException e) {
            throw new InternalException("整数格式错误：" + value + "，sheet：" + sheetName + "，行：" + (rowIndex) + "，列：" + (columnIndex));
        }
    }

    public Integer getNullableInteger() {
        Long l = this.getNullableLong();
        return l == null ? null : l.intValue();
    }

    public Integer getRequiredInteger() {
        return this.getRequiredLong().intValue();
    }

    public LocalDate getRequiredDate() {
        String value = getRequiredString();
        return parseLocalDate(value);
    }

    private LocalDate parseLocalDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(value, SLASH_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    Date date = cell.getDateCellValue();
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } catch (Exception e) {
                    throw new InternalException("日期格式错误：" + value + "，sheet：" + sheetName + "，行：" + (rowIndex) + "，列：" + (columnIndex));
                }
            }
        }
    }

    public String getNullableString() {
        if (cell == null) {
            return null;
        }
        CellType type = cell.getCellTypeEnum();
        String value;
        switch (type) {
            case NUMERIC:
                value = new BigDecimal(Double.toString(cell.getNumericCellValue())).toPlainString();
                if (value.endsWith(".0")) {
                    value = value.replace(".0", "");
                }
                break;
            case BOOLEAN:
                value = Boolean.toString(cell.getBooleanCellValue());
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case ERROR:
                value = Byte.toString(cell.getErrorCellValue());
                break;
            default:
                value = null;
        }
        return StringUtils.isBlank(value) ? null : value.trim();
    }

    public Double getNullableDouble() {
        String value = this.getNullableString();
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value).doubleValue();
        } catch (NumberFormatException e) {
            throw new InternalException("小数格式错误：" + value + "，sheet：" + sheetName + "，行：" + (rowIndex) + "，列：" + (columnIndex));
        }
    }

    public Double getRequiredDouble() {
        String value = getRequiredString();
        try {
            return new BigDecimal(value).doubleValue();
        } catch (NumberFormatException e) {
            throw new InternalException("小数格式错误：" + value + "，sheet：" + sheetName + "，行：" + (rowIndex) + "，列：" + (columnIndex));
        }
    }
}
