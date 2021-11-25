package top.felixu.platform.exception;

import lombok.Getter;

/**
 * @author felixu
 * @since 2021.11.25
 */
@Getter
public class InternalException extends PlatformException{

    public InternalException(ErrorCode error) {
        super(error);
    }

    public InternalException(ErrorCode error, Object... params) {
        super(error, params);
    }
}
