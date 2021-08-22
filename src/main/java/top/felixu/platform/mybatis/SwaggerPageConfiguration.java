package top.felixu.platform.mybatis;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.schema.configuration.ModelsConfiguration;

/**
 * @author felixu
 * @since 2021.08.07
 */
@Configuration
@ConditionalOnClass(ModelsConfiguration.class)
@AutoConfigureAfter(ModelsConfiguration.class)
public class SwaggerPageConfiguration {

    @Bean
    public PageRequestRuleConvention pageRequestRuleConvention(@Autowired(required = false) TypeResolver typeResolver) {
        return new PageRequestRuleConvention(typeResolver);
    }
}
