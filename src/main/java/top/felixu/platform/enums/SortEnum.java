package top.felixu.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author felixu
 * @since 2021.09.08
 */
@Getter
@AllArgsConstructor
public enum SortEnum implements BaseEnum{

    DRAG("拖动"),
    UP("上移"),
    DOWN("下移"),
    TOP("置顶"),
    BOTTOM("置底");

    private final String desc;
}
