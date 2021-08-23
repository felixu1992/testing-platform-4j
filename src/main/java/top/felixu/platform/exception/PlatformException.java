package top.felixu.platform.exception;

import lombok.Getter;

import java.text.MessageFormat;

/**
 * 统一异常定义
 *
 * @author felixu
 * @since 2021.08.01
 */
@Getter
public class PlatformException extends RuntimeException {

    /**
     * 状态码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    private PlatformException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public PlatformException(ErrorCode error) {
        this(error.getCode(), error.getMessage());
    }

    public PlatformException(ErrorCode error, Object... params) {
        this(error.getCode(), MessageFormat.format(error.getMessage(), params));
    }
}
