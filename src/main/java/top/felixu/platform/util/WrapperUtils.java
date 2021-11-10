package top.felixu.platform.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author felixu
 * @since 2021.11.10
 */
public class WrapperUtils {

    public static <T, R, E> LambdaQueryWrapper<T> relation(LambdaQueryWrapper<T> wrapper, SFunction<T, R> column, Collection<E> collection, boolean condition) {
        if (condition && !CollectionUtils.isEmpty(collection))
            wrapper.in(column, collection);
        return wrapper;
    }
}
