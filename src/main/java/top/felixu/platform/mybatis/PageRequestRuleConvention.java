package top.felixu.platform.mybatis;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.core.Ordered;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.AlternateTypeRules;
import top.felixu.platform.model.form.PageRequestForm;

import java.util.List;

/**
 * @author felixu
 * @since 2021.08.07
 */
public class PageRequestRuleConvention implements AlternateTypeRuleConvention {

    private final TypeResolver typeResolver;

    public PageRequestRuleConvention(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public List<AlternateTypeRule> rules() {
        return Lists.newArrayList(AlternateTypeRules.newRule(typeResolver.resolve(PageRequestForm.class), typeResolver.resolve(SwaggerPage.class)));
    }

    @ApiModel
    @Data
    private static class SwaggerPage {
        @ApiModelProperty(value = "第几页", example = "1")
        private Integer current = 1;

        @ApiModelProperty(value = "每页数据量", example = "10")
        private Integer size = 10;

        @ApiModelProperty(value = "是否查询总数量", example = "true")
        private boolean searchCount = true;

        @ApiModelProperty(value = "排序参数，示例：xxoo.com?orders=user_name.asc&orders=register_age.desc", example = "user_name.asc")
        private List<String> orders;
    }
}
