package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.UserMapper;
import top.felixu.platform.model.entity.User;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author felixu
 * @since 2021-08-23
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {

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