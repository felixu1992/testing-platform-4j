package top.felixu.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import top.felixu.platform.model.entity.CaseInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 用例信息 Mapper 接口
 *
 * @author felixu
 * @since 2021-08-28
 */
public interface CaseInfoMapper extends BaseMapper<CaseInfo> {
    /**
     * 不做空值检查，根据主键强制更新除“created_at”之外的所有字段（可将字段更新为null）
     *
     * @param entity 目标实体对象
     * @return 受影响的数据量
     */
    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) CaseInfo entity);
}
