package top.felixu.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author felixu
 * @since 2021.09.08
 */
@Getter
@AllArgsConstructor
public enum HttpMethodEnum implements BaseEnum{

    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");

    private final String desc;
}
