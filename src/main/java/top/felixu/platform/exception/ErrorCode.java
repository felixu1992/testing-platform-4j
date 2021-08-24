package top.felixu.platform.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举类
 * @author felixu
 * @since 2021.08.21
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(0, "成功"),
    FAILED(-1, "发生未知错误"),

    PARAM_ERROR(100, "不合法的参数"),
    LOGIN_FAILED(101, "登录失败，请检查用户名密码是否正确"),
    MISSING_PARAM(102, "缺少必要参数"),
    PATH_PARAM_ERROR(103, "路径参数不合法"),
    PARAM_TYPE_ERROR(104, "参数类型不合法"),
    MISSING_AUTHORITY(105, "权限不足"),
    REQUIRE_LOGIN(106, "请(重新)登录"),

    USER_NOT_FOUND(1000, "当前用户不存在"),
    ;

    private final int code;

    private final String message;
}