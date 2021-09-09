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

    /* ------------------- 通用错误 --------------------*/

    SUCCESS(0, "成功"),
    FAILED(-1, "发生未知错误"),

    PARAM_ERROR(100, "不合法的参数"),
    LOGIN_FAILED(101, "登录失败，请检查密码是否正确，或者联系管理员重置密码"),
    MISSING_PARAM(102, "缺少必要参数"),
    PATH_PARAM_ERROR(103, "路径参数不合法"),
    PARAM_TYPE_ERROR(104, "参数类型不合法"),
    MISSING_AUTHORITY(105, "权限不足"),
    REQUIRE_LOGIN(106, "请(重新)登录"),

    /* ------------------- 用户错误 --------------------*/

    USER_NOT_FOUND(1000, "当前用户不存在"),
    SUPER_ADMIN_CAN_NOT_DELETE(1001, "超级管理员无法被删除"),
    ONLY_SUPPORT_CHANGE_SELF_PASSWORD(1002, "只能修改自己的密码哦，你别乱来"),
    ORIGINAL_PASSWORD_IS_WRONG(1003, "如果忘记了旧密码，请联系管理员重置密码"),
    DEFAULT_PASSWORD_NOT_SUPPORT(1004, "初始密码无法登陆的哦，请先修改密码"),
    CAN_NOT_USE_DEFAULT_PASSWORD(1005, "不可以修改为初始密码"),
    NEW_PASSWORD_SAME_ORIGINAL(1006, "您的新密码不可以与旧密码一致"),
    USER_DUPLICATE_EMAIL(1007, "用户邮箱已经被使用"),
    USER_DUPLICATE_PHONE(1008, "用户手机号已经被使用"),

    /* ------------------- 联系人错误 --------------------*/

    CONTACTOR_GROUP_NOT_FOUND(2000, "当前联系人分组不存在"),
    CONTACTOR_GROUP_DUPLICATE_NAME(2001, "当前联系人分组名称重复"),
    CONTACTOR_GROUP_USED_BY_CONTACTOR(2002, "当前联系人分组下存在联系人"),

    CONTACTOR_NOT_FOUND(2100, "当前联系不存在"),
    CONTACTOR_DUPLICATE_EMAIL(2101, "联系人邮箱已经被使用"),
    CONTACTOR_DUPLICATE_PHONE(2102, "联系人手机号已经被使用"),
    CONTACTOR_USED_BY_CASE(2103, "当前联系人正在被用例使用"),

    /* ------------------- 文件错误 --------------------*/

    FILE_GROUP_NOT_FOUND(3000, "当前文件分组不存在"),
    FILE_GROUP_DUPLICATE_NAME(3001, "当前文件分组名称重复"),
    FILE_GROUP_USED_BY_FILE(3002, "当前文件分组下存在文件"),

    FILE_NOT_FOUND(3100, "当前文件不存在"),
    FILE_DUPLICATE_NAME(3101, "文件名称已经被使用"),
    FILE_SAVE_FAILED(3102, "文件存储失败"),
    FILE_UPDATE_FAILED(3102, "文件更新失败"),
    FILE_DELETE_FAILED(3102, "文件删除失败"),

    /* ------------------- 项目错误 --------------------*/

    PROJECT_GROUP_NOT_FOUND(4000, "当前项目分组不存在"),
    PROJECT_GROUP_DUPLICATE_NAME(4001, "当前项目分组名称重复"),
    PROJECT_GROUP_USED_BY_PROJECT(4002, "当前项目分组下存在项目"),

    PROJECT_NOT_FOUND(4100, "当前项目不存在"),
    PROJECT_DUPLICATE_NAME(4101, "项目名称已经被使用"),
    PROJECT_USED_BY_CASE(4102, "当前项目下存在用例"),

    /* ------------------- 用例错误 --------------------*/

    CASE_GROUP_NOT_FOUND(5000, "当前用例分组不存在"),
    CASE_GROUP_DUPLICATE_NAME(5001, "当前用例分组名称重复"),

    CASE_NOT_FOUND(5100, "当前用例不存在"),
    CASE_DUPLICATE_NAME(5101, "用例名称已经被使用"),
    CASE_DRAG_MISS_TARGET(5102, "用例拖动缺少落点位置"),
    CASE_MOVE_OPERATION_ERROR(5103, "未知的移动操作"),
    ;

    private final int code;

    private final String message;
}