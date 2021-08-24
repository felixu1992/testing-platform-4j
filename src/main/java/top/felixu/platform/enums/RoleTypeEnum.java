package top.felixu.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author felixu
 * @since 2021.08.24
 */
@Getter
@AllArgsConstructor
public enum RoleTypeEnum implements BaseEnum {

    SUPER_ADMIN("超级管理员"),
    ADMIN("管理员"),
    ORDINARY("普通用户"),;

    private final String desc;
}
