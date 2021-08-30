package top.felixu.platform.util;

import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.User;

import java.util.Optional;

/**
 * @author felixu
 * @since 2021.08.24
 */
public class UserHolderUtils {

    private final static ThreadLocal<User> USER = new ThreadLocal<>();

    public static void setUser(User user) {
        USER.set(user);
    }

    public static Integer getCurrentUserId() {
        return getUser().getId();
    }

    public static RoleTypeEnum getCurrentRole() {
        return getUser().getRole();
    }

    public static Integer getCurrentUserParentId() {
        return getUser().getParentId();
    }

    private static User getUser() {
        return Optional.ofNullable(USER.get()).orElseThrow(() -> new PlatformException(ErrorCode.REQUIRE_LOGIN));
    }

    public static void clear() {
        USER.remove();
    }
}
