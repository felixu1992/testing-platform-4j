package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * @author felixu
 * @since 2021.08.07
 */
@Slf4j
public class SqlInjector extends DefaultSqlInjector {

    private final String underscoreCreateTime;

    public SqlInjector(MybatisPlusProperties properties) {
        underscoreCreateTime = properties.getUnderscoreCreateTime();
    }

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = new ArrayList<>();
        methodList.add(new AlwaysUpdateSomeColumnById().setPredicate(i -> !underscoreCreateTime.equalsIgnoreCase(i.getColumn())));
        methodList.addAll(super.getMethodList(mapperClass));
        return methodList;
    }
}
