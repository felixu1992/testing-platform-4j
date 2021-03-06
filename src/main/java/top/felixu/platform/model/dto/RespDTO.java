package top.felixu.platform.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import top.felixu.platform.exception.ErrorCode;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * 统一返回结构体封装
 *
 * @author felixu
 * @since 2021.08.01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespDTO<T> implements Serializable {

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 结果集
     */
    private T data;

    public static <T> RespDTO<T> success() {
        return success(null);
    }

    public static <T> RespDTO<T> success(T data) {
        return build(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    public static <T> RespDTO<T> fail(ErrorCode error) {
        return fail(error.getCode(), error.getMessage(), null);
    }

    public static <T> RespDTO<T> fail(BindingResult result) {
        String errorMsg = result.getAllErrors().stream().map(objectError -> {
            FieldError error = (FieldError) objectError;
            return error.getField() + ": " + error.getDefaultMessage();
        }).collect(Collectors.joining(" & "));
        return fail(ErrorCode.PARAM_ERROR.getCode(), errorMsg, null);
    }

    public static <T> RespDTO<T> fail(int code, String message, T data) {
        return build(code, message, data);
    }

    private static <T> RespDTO<T> build(int code, String message, T data) {
        return new RespDTO<T>(code, message, data);
    }
}
