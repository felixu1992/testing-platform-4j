package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import java.time.LocalDateTime;

/**
 * 部分字段的自动注入
 *
 * @author felixu
 * @since 2021.08.07
 */
public class AuditTimeMetaObjectHandler implements MetaObjectHandler {

    private final String camelCreateTime;

    private final String camelUpdateTime;

    public AuditTimeMetaObjectHandler(MybatisPlusProperties properties) {
        camelCreateTime = properties.getCamelCreateTime();
        camelUpdateTime = properties.getCamelUpdateTime();
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, camelCreateTime, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, camelUpdateTime, LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, camelUpdateTime, LocalDateTime.class, LocalDateTime.now());
    }
}
