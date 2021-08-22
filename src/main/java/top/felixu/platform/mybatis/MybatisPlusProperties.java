package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private String camelCreateTime = "createTime";
    /**
     * 驼峰式更新时间字段
     */
    private String camelUpdateTime = "updateTime";
    /**
     * 下划线式创建时间字段
     */
    private String underscoreCreateTime = "create_time";
    /**
     * 下划线式更新时间字段
     */
    private String underscoreUpdateTime = "update_time";
    /**
     * 数据库类型
     */
    private DbType dbType;
    /**
     * 最大分页限制
     */
    private Long maxLimit = 200L;
}
