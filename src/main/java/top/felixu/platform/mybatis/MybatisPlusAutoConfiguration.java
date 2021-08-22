package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author felixu
 * @since 2021.08.07
 */
@Configuration
@PropertySource("classpath:/mybatis-plus-default.properties")
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusAutoConfiguration {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
        pageInterceptor.setDbType(properties.getDbType());
        pageInterceptor.setMaxLimit(properties.getMaxLimit());
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(ISqlInjector.class)
    public SqlInjector sqlInjector(MybatisPlusProperties mybatisProperties) {
        return new SqlInjector(mybatisProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "auditTimeMetaObjectHandler")
    public AuditTimeMetaObjectHandler auditTimeMetaObjectHandler(MybatisPlusProperties mybatisProperties) {
        return new AuditTimeMetaObjectHandler(mybatisProperties);
    }
}
