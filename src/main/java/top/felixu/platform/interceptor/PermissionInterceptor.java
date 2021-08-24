package top.felixu.platform.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.felixu.platform.constants.PermissionConstants;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.properties.PermissionProperties;
import top.felixu.platform.service.UserService;
import top.felixu.platform.util.JwtUtils;
import top.felixu.platform.util.UserHolderUtils;

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

    private final PermissionProperties properties;

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 到了这里，一定是需要拦截的
        String token = request.getHeader(properties.getAuthorization());
        Integer userId = null;
        if (StringUtils.hasText(token)) {
            // 取出 token
            token = token.replace(properties.getSalt(), "");
            // 从 token 中解析出 userId
            userId = JwtUtils.getUserId(token);
            String cacheToken = redisTemplate.opsForValue().get(userId);
            if (null == cacheToken)
                throw new PlatformException(ErrorCode.REQUIRE_LOGIN);
            // 判断与存的是否一样，不一样，则认为被挤掉了
            if (!token.equals(cacheToken))
                throw new PlatformException(ErrorCode.REQUIRE_LOGIN);
            redisTemplate.expire(String.valueOf(userId), properties.getTimeout());
        } else {
            String secret = request.getHeader(properties.getSecret());
            if (StringUtils.hasText(secret)) {
                // 看看 secret 能不能找到用户，找到就 ok
                User user = userService.getUserBySecretAndCheck(secret);
                userId = user.getId();
            }
        }
        if (ObjectUtils.isEmpty(userId))
            throw new PlatformException(ErrorCode.REQUIRE_LOGIN);
        UserHolderUtils.setUserId(userId);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolderUtils.clear();
    }
}