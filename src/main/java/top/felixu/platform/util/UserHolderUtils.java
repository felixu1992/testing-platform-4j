package top.felixu.platform.util;

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

    public static Optional<Integer> getCurrentUserId() {
        return Optional.ofNullable(USER_ID.get());
    }

    public static void clear() {
        USER_ID.remove();
    }
}
