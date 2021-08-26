package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.felixu.platform.constants.CacheKeyConstants;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.model.form.LoginForm;
import top.felixu.platform.properties.PermissionProperties;
import top.felixu.platform.service.UserService;
import top.felixu.platform.util.JwtUtils;
import top.felixu.platform.util.Md5Utils;
import top.felixu.platform.util.UserHolderUtils;

/**
 * @author felixu
 * @since 2021.08.26
 */
@Service
@RequiredArgsConstructor
public class UserManager {

    private final UserService userService;

    private final StringRedisTemplate redisTemplate;

    private final PermissionProperties properties;

    public User login(LoginForm form) {
        // 根据帐号密码，查询用户
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, form.getEmail())
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

    public User getUserById(Integer id) {
        // 先判断自己是谁，有没有资格看人家信息
        Integer selfId = UserHolderUtils.getCurrentUserId().orElseThrow(() -> new PlatformException(ErrorCode.MISSING_AUTHORITY));
        User self = userService.getUserByIdAndCheck(selfId);
        // 超级管理员看所有，管理员看自己及以下，普通用户只能看自己
        if ((self.getRole() == RoleTypeEnum.ORDINARY && !selfId.equals(id))
                || (self.getRole() == RoleTypeEnum.ADMIN ))
    }
}
