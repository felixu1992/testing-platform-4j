package top.felixu.platform.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.felixu.platform.constants.PermissionConstants;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截器
 *
 * @author felixu
 * @since 2021.08.23
 */
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 到了这里，一定是需要拦截的
        // 取 token
        // 取 secret
        String token = request.getHeader(PermissionConstants.Key.AUTHORIZATION);
        if (StringUtils.hasText(token)) {
            // 取出 token
            token = token.replace(PermissionConstants.Key.TOKEN_PREFIX, "");
            // 从 token 中解析出 userId
            String userId = "userId";
            String cacheToken = redisTemplate.opsForValue().get(userId);
            // 判断是否存在，是否过期
            // 得到用户信息，存一下
        }
        String secret = request.getHeader(PermissionConstants.Key.SECRET);
        if (StringUtils.hasText(secret)) {
            // 处理 secret
        }
        throw new PlatformException(ErrorCode.REQUIRE_LOGIN);
    }
}