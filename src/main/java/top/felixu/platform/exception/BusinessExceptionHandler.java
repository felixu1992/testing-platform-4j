package top.felixu.platform.exception;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import top.felixu.platform.model.dto.ResponseDTO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 统一异常捕获
 *
 * @author felixu
 * @since 2021.08.01
 */
@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

    public static final String INTERNAL_SERVER_ERROR_MESSAGE_DEFAULT = "Oops_服务器开小差啦，过会儿再试吧_\uD83D\uDE02\uD83D\uDE02\uD83D\uDE02";

    /**
     * 是否运行在生产环境
     */
    private final boolean isProd;

    public BusinessExceptionHandler(Environment environment) {
        this.isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    /*------------------------------- 处理 Business Exception -------------------------------*/

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDTO<?>> handleBusinessEx(BusinessException ex, HttpServletRequest request) {
        log.error("---> 业务异常: {} {}", request.getMethod(), request.getRequestURI());
        log.error("---> error message: ", ex);
        return new ResponseEntity<>(ResponseDTO.fail(ex.getCode(), ex.getMessage()), HttpStatus.OK);
    }

    /*------------------------------- 参数校验出错的处理 -------------------------------*/

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO<?>> handleMissingParameterEx(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String name = ex.getParameterName();
        String type = ex.getParameterType();
        log.error("---> 缺少必传参数：{} {}, {}({})", request.getMethod(), request.getRequestURI(), name, type);
        log.error("---> error message：", ex);
        ErrorCode error = ErrorCode.MISSING_PARAM;
        String message = name + ": " + error.getMessage();
        return new ResponseEntity<>(ResponseDTO.fail(error.getCode(), message), HttpStatus.OK);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDTO<?>> handleBindEx(BindException ex, HttpServletRequest request) {
        ResponseDTO<Object> fail = ResponseDTO.fail(ex);
        log.error("---> 参数校验失败：{} {}, {}", request.getMethod(), request.getRequestURI(), fail.getMessage());
        log.error("---> error message：", ex);
        return new ResponseEntity<>(fail, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> handleMethodArgumentNotValidEx(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ResponseDTO<Object> fail = ResponseDTO.fail(ex.getBindingResult());
        log.error("---> 参数校验失败：{} {}, {}", request.getMethod(), request.getRequestURI(), fail.getMessage());
        log.error("---> error message：", ex);
        return new ResponseEntity<>(fail, HttpStatus.OK);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDTO<?>> handleConstraintViolationEx(ConstraintViolationException ex, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> result = new HashMap<>(violations.size());
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            result.put(fieldName, violation.getMessage());
        }
        BindException exception = new BindException(ex, "exception");
        result.forEach((key, value) -> exception.addError(new FieldError(exception.getObjectName(), key, value)));
        ResponseDTO<Object> fail = ResponseDTO.fail(exception.getBindingResult());
        log.error("---> 参数校验失败：{} {}, {}", request.getMethod(), request.getRequestURI(), fail.getMessage());
        log.error("---> error message：", ex);
        return new ResponseEntity<>(fail, HttpStatus.OK);
    }

    /*------------------------------- 没有登录 -------------------------------*/

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO<?>> handleAuthenticationEx() {
        return new ResponseEntity<>(ResponseDTO.fail(ErrorCode.REQUIRE_LOGIN), HttpStatus.UNAUTHORIZED);
    }

    /*------------------------------- 没有权限 -------------------------------*/

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ResponseDTO<?>> handleAuthorityEx() {
        return new ResponseEntity<>(ResponseDTO.fail(ErrorCode.MISSING_AUTHORITY), HttpStatus.FORBIDDEN);
    }

    /*------------------------------- 其他异常 -------------------------------*/

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDTO<?>> argumentExHandler(Throwable ex, HttpServletRequest request) {
        log.error("---> 路径参数不合法：{} {}", request.getMethod(), request.getRequestURI());
        log.error("---> error message：", ex);
        return new ResponseEntity<>(ResponseDTO.fail(ErrorCode.PATH_PARAM_ERROR), HttpStatus.OK);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<?>> enumArgumentExHandler(Throwable ex, HttpServletRequest request) {
        log.error("---> 参数类型不合法：{} {}", request.getMethod(), request.getRequestURI(), ex);
        log.error("---> error message：", ex);
        return new ResponseEntity<>(ResponseDTO.fail(ErrorCode.PARAM_TYPE_ERROR), HttpStatus.OK);
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<ResponseDTO<?>> handleEx(Throwable ex, HttpServletRequest request) {
        log.error("---> 未知异常：{} {}", request.getMethod(), request.getRequestURI());
        log.error("---> error message：", ex);
        ErrorCode error = ErrorCode.FAILED;
        if (isProd) {
            return new ResponseEntity<>(ResponseDTO.fail(error.getCode(), INTERNAL_SERVER_ERROR_MESSAGE_DEFAULT), HttpStatus.OK);
        }
        String message = Throwables.getStackTraceAsString(ex);
        return new ResponseEntity<>(ResponseDTO.fail(error.getCode(), message), HttpStatus.OK);
    }
}
