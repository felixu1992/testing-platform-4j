package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.UserMapper;
import top.felixu.platform.model.entity.User;
import java.util.Collections;
import java.util.List;
import static top.felixu.platform.constants.CacheKeyConstants.User.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.User.USER_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.User.CHILD_USER_CACHE;

/**
 * 服务实现类
 *
 * @author felixu
 * @since 2021-08-23
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {

    @Cacheable(cacheNames = NAME, key = USER_CACHE + " + #id", unless = "#result == null", sync = true)
    public User getUserByIdAndCheck(Integer id) {
        User user = getById(id);
        if (null == user)
            throw new PlatformException(ErrorCode.USER_NOT_FOUND);
        return user;
    }

    @Cacheable(cacheNames = NAME, key = USER_CACHE + " + #secret", unless = "#result == null", sync = true)
    public User getUserBySecretAndCheck(String secret) {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getSecret, secret));
        if (null == user)
            throw new PlatformException(ErrorCode.USER_NOT_FOUND);
        return user;
    }

    @Cacheable(cacheNames = NAME, key = CHILD_USER_CACHE + " + #user.getId()", unless = "#result == null", sync = true)
    public List<User> getChildUserList(User user) {
        if (user.getRole() == RoleTypeEnum.SUPER_ADMIN)
            return list();
        if (user.getRole() == RoleTypeEnum.ADMIN)
            return list(Wrappers.<User>lambdaQuery().eq(User::getParentId, user.getId()));
        return Collections.emptyList();
    }

    @Caching(
            evict = @CacheEvict(cacheNames = NAME, key = CHILD_USER_CACHE + " + #user.getParentId()"),
            cacheable = {
                    @Cacheable(cacheNames = NAME, key = USER_CACHE + " + #user.getId()", unless = "#result == null", sync = true),
                    @Cacheable(cacheNames = NAME, key = USER_CACHE + " + #user.getSecret()", unless = "#result == null", sync = true)
            }
    )
    public User create(User user) {
        save(user);
        return user;
    }

    @Caching(
            evict = @CacheEvict(cacheNames = NAME, key = CHILD_USER_CACHE + " + #user.getParentId()"),
            put = {
                    @CachePut(cacheNames = NAME, key = USER_CACHE + " + #user.getId()", unless = "#result == null"),
                    @CachePut(cacheNames = NAME, key = USER_CACHE + " + #user.getSecret()", unless = "#result == null")
            }
    )
    public User update(User user) {
        updateById(user);
        return user;
    }


    @Caching(
            evict = {
                    @CacheEvict(cacheNames = NAME, key = USER_CACHE + " + #user.getId()"),
                    @CacheEvict(cacheNames = NAME, key = USER_CACHE + " + #user.getSecret()"),
                    @CacheEvict(cacheNames = NAME, key = CHILD_USER_CACHE + " + #user.getParentId()")
            }
    )
    public void delete(User user) {
        removeById(user.getId());
    }
}