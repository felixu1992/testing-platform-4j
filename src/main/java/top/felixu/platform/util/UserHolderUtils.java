package top.felixu.platform.util;

import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;

import java.util.Optional;

/**
 * @author felixu
 * @since 2021.08.24
 */
public class UserHolderUtils {

    private final static ThreadLocal<Integer> USER_ID = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        USER_ID.set(userId);
    }

    public static Integer getCurrentUserId() {
        return Optional.ofNullable(USER_ID.get()).orElseThrow(() -> new PlatformException(ErrorCode.REQUIRE_LOGIN));
    }

    public static void clear() {
        USER_ID.remove();
    }
}
