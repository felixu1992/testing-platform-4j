package top.felixu.platform.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import top.felixu.platform.util.UserHolderUtils;

/**
 * @author felixu
 * @since 2021.08.31
 */
@RequiredArgsConstructor
public class OwnerTenantLineHandler implements TenantLineHandler {

    private final MybatisPlusProperties properties;

    @Override
    public Expression getTenantId() {
        return new StringValue(String.valueOf(UserHolderUtils.getOwner()));
    }

    @Override
    public String getTenantIdColumn() {
        return "owner";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return properties.getIgnores().contains(tableName);
    }
}
