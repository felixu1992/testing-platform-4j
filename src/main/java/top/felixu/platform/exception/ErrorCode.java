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
    LOGIN_FAILED(101, "登录失败，请检查密码是否正确，或者联系管理员重置密码"),
    MISSING_PARAM(102, "缺少必要参数"),
    PATH_PARAM_ERROR(103, "路径参数不合法"),
    PARAM_TYPE_ERROR(104, "参数类型不合法"),
    MISSING_AUTHORITY(105, "权限不足"),
    REQUIRE_LOGIN(106, "请(重新)登录"),

    USER_NOT_FOUND(1000, "当前用户不存在"),
    SUPER_ADMIN_CAN_NOT_DELETE(1001, "超级管理员无法被删除"),
    ONLY_SUPPORT_CHANGE_SELF_PASSWORD(1002, "只能修改自己的密码哦，你别乱来"),
    ORIGINAL_PASSWORD_IS_WRONG(1003, "如果忘记了旧密码，请联系管理员重置密码"),
    DEFAULT_PASSWORD_NOT_SUPPORT(1004, "初始密码无法登陆的哦，请先修改密码"),
    CAN_NOT_USE_DEFAULT_PASSWORD(1005, "不可以修改为初始密码"),
    NEW_PASSWORD_SAME_ORIGINAL(1006, "您的新密码不可以与旧密码一致"),
    USER_DUPLICATE_EMAIL(1007, "用户邮箱已经被使用"),
    USER_DUPLICATE_PHONE(1008, "用户手机号已经被使用"),

    CONTACTOR_GROUP_NOT_FOUND(2000, "当前联系人分组不存在"),
    CONTACTOR_GROUP_DUPLICATE_NAME(2001, "当前联系人分组名称重复"),
    ;

    private final int code;

    private final String message;
}