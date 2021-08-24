package top.felixu.platform.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

/**
 * 从 Spring IoC 容器中获取 Bean
 *
 * @author felixu
 * @since 2021.08.24
 */
public class ApplicationUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 根据 Bean 的名称获取 Bean
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 根据 Bean 的类型获取 Bean
     */
    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }
}
