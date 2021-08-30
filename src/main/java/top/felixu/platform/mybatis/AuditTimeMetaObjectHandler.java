package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.util.UserHolderUtils;

import java.time.LocalDateTime;

/**
 * 部分字段的自动注入
 *
 * @author felixu
 * @since 2021.08.07
 */
public class AuditTimeMetaObjectHandler implements MetaObjectHandler {

    private final String camelCreatedAt;

    private final String camelCreatedBy;

    private final String camelUpdatedAt;

    private final String camelUpdatedBy;

    private final String owner;

    public AuditTimeMetaObjectHandler(MybatisPlusProperties properties) {
        camelCreatedAt = properties.getCamelCreatedAt();
        camelCreatedBy = properties.getCamelCreateBy();
        camelUpdatedAt = properties.getCamelUpdatedAt();
        camelUpdatedBy = properties.getCamelUpdatedBy();
        owner =  properties.getOwner();
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Integer userId = UserHolderUtils.getCurrentUserId();
        this.strictInsertFill(metaObject, camelCreatedAt, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, camelCreatedBy, Integer.class, userId);
        this.strictInsertFill(metaObject, camelUpdatedAt, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, camelUpdatedBy, Integer.class, userId);
        this.strictInsertFill(metaObject, owner, Integer.class, UserHolderUtils.getCurrentRole() == RoleTypeEnum.ORDINARY ? UserHolderUtils.getCurrentUserParentId() : userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, camelUpdatedAt, LocalDateTime.class, LocalDateTime.now());
    }
}