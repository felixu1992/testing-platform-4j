package top.felixu.platform.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.service.UserProjectService;

import java.util.List;

/**
 * @author felixu
 * @since 2021.11.10
 */
public class WrapperUtils {

    public static <T, R, E> LambdaQueryWrapper<T> relation(LambdaQueryWrapper<T> wrapper, SFunction<T, R> column,
                                                           UserProjectService userProjectService) {
        if (UserHolderUtils.getCurrentRole() != RoleTypeEnum.ORDINARY)
            return wrapper;
        return relation(wrapper, column, userProjectService.getProjectIdsByUserId(UserHolderUtils.getCurrentUserId()), true);
    }

    public static <T, R, E> LambdaQueryWrapper<T> relation(LambdaQueryWrapper<T> wrapper, SFunction<T, R> column,
                                                           List<Integer> projectIds, boolean condition) {
        wrapper.in(condition && !CollectionUtils.isEmpty(projectIds), column, projectIds);
        return wrapper;
    }
}
