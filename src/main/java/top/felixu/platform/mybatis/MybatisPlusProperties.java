package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author felixu
 * @since 2021.08.07
 */
@Data
@ConfigurationProperties(prefix = "platform.mybatis")
public class MybatisPlusProperties {
    /**
     * 驼峰式创建时间字段
     */
    private String camelCreatedAt = "createdAt";

    /**
     * 驼峰式创建者字段
     */
    private String camelCreateBy = "createdBy";

    /**
     * 驼峰式更新时间字段
     */
    private String camelUpdatedAt = "updatedAt";

    /**
     * 驼峰式更新者字段
     */
    private String camelUpdatedBy = "updatedBy";

    /**
     * 下划线式创建时间字段
     */
    private String underscoreCreatedAt = "created_at";

    /**
     * 下划线式创建者字段
     */
    private String underscoreCreatedBy = "created_by";

    /**
     * 下划线式更新时间字段
     */
    private String underscoreUpdatedAt = "updated_at";

    /**
     * 下划线更新者字段
     */
    private String underscoreUpdatedBy = "updated_by";

    /**
     * 数据拥有者字段
     */
    private String owner = "owner";

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 最大分页限制
     */
    private Long maxLimit = 200L;

    /**
     * 忽略 owner 字段查询的表
     */
    private Set<String> ignores = new HashSet<>();
}
