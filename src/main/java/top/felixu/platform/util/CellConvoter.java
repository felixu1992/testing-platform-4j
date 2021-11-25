package top.felixu.platform.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public interface CellConvoter {

    Object convoterTo(Row row);
}
