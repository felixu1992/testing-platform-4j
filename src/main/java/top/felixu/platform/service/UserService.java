package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.felixu.platform.constants.CacheKeyConstants;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.UserMapper;
import top.felixu.platform.model.entity.User;
import org.springframework.stereotype.Service;
import top.felixu.platform.model.form.LoginForm;
import top.felixu.platform.properties.PermissionProperties;
import top.felixu.platform.util.JwtUtils;
import top.felixu.platform.util.Md5Utils;
import top.felixu.platform.util.UserHolderUtils;

/**
 *  服务实现类
 *
 * @author felixu
 * @since 2021-08-23
 */
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {

    private final StringRedisTemplate redisTemplate;

    private final PermissionProperties properties;

    public User login(LoginForm form) {
        // 根据帐号密码，查询用户
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, form.getEmail())
                .eq(User::getPassword, Md5Utils.encode(form.getPassword())));
        if (null == user)
            throw new PlatformException(ErrorCode.LOGIN_FAILED);
        // 登录成功，设置 token
        user.setToken(JwtUtils.generate(user.getId()));
        // 缓存 token，并设置过期时间(这个会顶掉之前的登录，一个帐号只能一处登录)
        redisTemplate.opsForValue().set(CacheKeyConstants.Token.PREFIX + user.getId(), user.getToken(), properties.getTimeout());
        return user;
    }

    public void logout() {
        UserHolderUtils.getCurrentUserId().ifPresent(userId -> redisTemplate.delete(CacheKeyConstants.Token.PREFIX + userId));
    }

    @Cacheable(cacheNames = "USER", key = "'USERCACHE-' + #id", unless = "#result == null", sync = true)
    public User getUserByIdAndCheck(int id) {
        User user = getById(id);
        if (null == user)
            throw new PlatformException(ErrorCode.USER_NOT_FOUND);
        return user;
    }

    @Cacheable(cacheNames = "USER", key = "'USERCACHE-' + #secret", unless = "#result == null", sync = true)
    public User getUserBySecretAndCheck(String secret) {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getSecret, secret));
        if (null == user)
            throw new PlatformException(ErrorCode.USER_NOT_FOUND);
        return user;
    }
}