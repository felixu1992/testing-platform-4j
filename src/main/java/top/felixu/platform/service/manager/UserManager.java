package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.constants.CacheKeyConstants;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.dto.UserDTO;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.model.form.ChangePasswordForm;
import top.felixu.platform.model.form.LoginForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.form.UserForm;
import top.felixu.platform.properties.PermissionProperties;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.UserProjectService;
import top.felixu.platform.service.UserService;
import top.felixu.platform.util.JwtUtils;
import top.felixu.platform.util.Md5Utils;
import top.felixu.platform.util.RandomStringUtils;
import top.felixu.platform.util.UserHolderUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author felixu
 * @since 2021.08.26
 */
@Service
@RequiredArgsConstructor
public class UserManager {

    private final UserService userService;

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    private final StringRedisTemplate redisTemplate;

    private final PermissionProperties properties;

    public RespDTO<User> login(LoginForm form) {
        // 根据帐号密码，查询用户
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, form.getEmail())
                .eq(User::getPassword, Md5Utils.encode(form.getPassword())));
        if (null == user)
            throw new PlatformException(ErrorCode.LOGIN_FAILED);
        if (user.getPassword().equals(properties.getDefaultPassword()))
            return RespDTO.fail(ErrorCode.DEFAULT_PASSWORD_NOT_SUPPORT.getCode(), ErrorCode.DEFAULT_PASSWORD_NOT_SUPPORT.getMessage(), user);
        // 登录成功，设置 token
        user.setToken(JwtUtils.generate(user.getId()));
        // 缓存 token，并设置过期时间(这个会顶掉之前的登录，一个帐号只能一处登录)
        redisTemplate.opsForValue().set(CacheKeyConstants.Token.PREFIX + user.getId(), user.getToken(), properties.getTimeout());
        return RespDTO.success(user);
    }

    public void logout() {
        redisTemplate.delete(CacheKeyConstants.Token.PREFIX + UserHolderUtils.getCurrentUserId());
    }

    public UserDTO getUserById(Integer id) {
        // 先判断自己是谁，有没有资格看人家信息
        Integer selfId = UserHolderUtils.getCurrentUserId();
        User self = userService.getUserByIdAndCheck(selfId);
        checkAuthority(self, id);
        User user = userService.getUserByIdAndCheck(id);
        UserDTO result = BeanUtils.map(user, UserDTO.class);
        if (self.getRole() == RoleTypeEnum.ADMIN)
            result.setProjectIds(userProjectService.getProjectIdsByUserId(result.getId()));
        return result;
    }

    public IPage<User> page(User user, PageRequestForm form) {
        User self = userService.getUserByIdAndCheck(UserHolderUtils.getCurrentUserId());
        if (self.getRole() == RoleTypeEnum.ORDINARY)
            throw new PlatformException(ErrorCode.MISSING_AUTHORITY);
        if (self.getRole() == RoleTypeEnum.ADMIN)
            user.setParentId(self.getId());
        return userService.page(form.toPage(), Wrappers.lambdaQuery(user).eq(self.getRole() == RoleTypeEnum.SUPER_ADMIN, User::getRole, RoleTypeEnum.ADMIN));
    }

    public User create(UserForm user){
        User self = userService.getUserByIdAndCheck(UserHolderUtils.getCurrentUserId());
        if (self.getRole() == RoleTypeEnum.ORDINARY)
            throw new PlatformException(ErrorCode.MISSING_AUTHORITY);
        check(user);
        user.setRole(self.getRole() == RoleTypeEnum.SUPER_ADMIN ? RoleTypeEnum.ADMIN : RoleTypeEnum.ORDINARY);
        user.setParentId(self.getId());
        user.setPassword(properties.getDefaultPassword());
        user.setSecret(Base64Utils.encodeToString(RandomStringUtils.make().getBytes(StandardCharsets.UTF_8)));
        userService.create(user);
        if (UserHolderUtils.getCurrentRole() == RoleTypeEnum.ADMIN)
            updateRelation(user.getId(), user.getProjectIds());
        return user;
    }

    public User update(UserForm user) {
        User self = userService.getUserByIdAndCheck(UserHolderUtils.getCurrentUserId());
        User other = userService.getUserByIdAndCheck(user.getId());
        BeanUtils.copyNotEmpty(User.class, user, User.class, other);
        check(other);
        // 防止不该更新的字段被更新
        other.setPassword(null);
        other.setSecret(null);
        checkAuthority(self, other.getId());
        userService.update(other);
        if (UserHolderUtils.getCurrentRole() == RoleTypeEnum.ADMIN)
            updateRelation(user.getId(), user.getProjectIds());
        return other;
    }

    public void delete(Integer id) {
        User self = userService.getUserByIdAndCheck(UserHolderUtils.getCurrentUserId());
        checkAuthority(self, id);
        User user = userService.getUserByIdAndCheck(id);
        if (user.getRole() == RoleTypeEnum.SUPER_ADMIN)
            throw new PlatformException(ErrorCode.SUPER_ADMIN_CAN_NOT_DELETE);
        userService.delete(user);
        userProjectService.deleteRelationByUserId(id);
        // TODO: 08/27 删除项目与用户的关联关系
        // TODO: 08/27 如果是管理员，要清除所有数据
    }

    public User resetSecret(Integer id) {
        User self = userService.getUserByIdAndCheck(UserHolderUtils.getCurrentUserId());
        checkAuthority(self, id);
        User user = userService.getUserByIdAndCheck(id);
        user.setSecret(Base64Utils.encodeToString(RandomStringUtils.make().getBytes(StandardCharsets.UTF_8)));
        return userService.update(user);
    }

    public User changePassword(Integer id, ChangePasswordForm form) {
        // 只能自己修改密码，其他上级管理员只能重置密码
        if (!UserHolderUtils.getCurrentUserId().equals(id))
            throw new PlatformException(ErrorCode.ONLY_SUPPORT_CHANGE_SELF_PASSWORD);
        // TODO: 08/28 可以加个密码规则
        User user = userService.getUserByIdAndCheck(id);
        // 判断旧密码是否正确
        if (!user.getPassword().equals(Md5Utils.encode(form.getOriginalPassword())))
            throw new PlatformException(ErrorCode.ORIGINAL_PASSWORD_IS_WRONG);
        // 判断新密码是否还是默认密码
        String newPassword = Md5Utils.encode(form.getNewPassword());
        if (properties.getDefaultPassword().equals(newPassword))
            throw new PlatformException(ErrorCode.CAN_NOT_USE_DEFAULT_PASSWORD);
        // 判断是否和原密码相同
        if (user.getPassword().equals(newPassword))
            throw new PlatformException(ErrorCode.NEW_PASSWORD_SAME_ORIGINAL);
        user.setPassword(newPassword);
        redisTemplate.delete(CacheKeyConstants.Token.PREFIX + id);
        return userService.update(user);
    }

    public User changeDefaultPassword(Integer id, ChangePasswordForm form) {
        // TODO: 08/28 可以加个密码规则
        User user = userService.getUserByIdAndCheck(id);
        // 判断密码是否为默认密码
        if (!user.getPassword().equals(properties.getDefaultPassword()))
            throw new PlatformException(ErrorCode.CURRENT_PASSWORD_NOT_DEFAULT);
        user.setPassword(Md5Utils.encode(form.getNewPassword()));
        redisTemplate.delete(CacheKeyConstants.Token.PREFIX + id);
        UserHolderUtils.setUser(user);
        return userService.update(user);
    }

    public User resetPassword(Integer id) {
        User user = userService.getUserByIdAndCheck(id);
        // 能够重置密码的人，一定是指定 id 的上级
        if (!UserHolderUtils.getCurrentUserId().equals(user.getParentId()))
            throw new PlatformException(ErrorCode.MISSING_AUTHORITY);
        user.setPassword(properties.getDefaultPassword());
        return userService.update(user);
    }

    private void updateRelation(Integer id, List<Integer> projectIds) {
        // 判断项目是否都存在
        if (!CollectionUtils.isEmpty(projectIds)) {
            List<Project> projects = projectService.listByProjectIds(projectIds);
            if (projects.size() != projectIds.size())
                throw new PlatformException(ErrorCode.PARAM_ERROR);
        }
        // 更新关联关系
        userProjectService.updateRelation(id, projectIds);
    }

    private void checkAuthority(User self, Integer target) {
        // 超级管理员看所有，管理员看自己及以下，普通用户只能看自己
        if ((self.getRole() == RoleTypeEnum.ORDINARY && !self.getId().equals(target))
                || (self.getRole() == RoleTypeEnum.ADMIN && userService.getChildUserList(self)
                .stream().noneMatch(user -> user.getId().equals(target)) && !self.getId().equals(target)))
            throw new PlatformException(ErrorCode.MISSING_AUTHORITY);
    }

    private void check(User user) {
        // 校验邮箱
        User email = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, user.getEmail()));
        if (email != null && (user.getId() == null || !user.getId().equals(email.getId())))
            throw new PlatformException(ErrorCode.USER_DUPLICATE_EMAIL);
        // 校验手机号
        User phone = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, user.getPhone()));
        if (phone != null && (user.getId() == null || !user.getId().equals(phone.getId())))
            throw new PlatformException(ErrorCode.USER_DUPLICATE_PHONE);
    }
}
