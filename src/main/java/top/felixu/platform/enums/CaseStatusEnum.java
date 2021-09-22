package top.felixu.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author felixu
 * @since 2021.09.13
 */
@Getter
@AllArgsConstructor
public enum CaseStatusEnum implements BaseEnum {

    PASSED("校验通过"),
    FAILED("校验失败"),
    IGNORED("忽略执行");

    private final String desc;
}
