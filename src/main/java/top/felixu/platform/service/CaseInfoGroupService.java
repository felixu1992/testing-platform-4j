package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.CaseInfoGroupMapper;
import top.felixu.platform.model.entity.CaseInfoGroup;
import org.springframework.stereotype.Service;

/**
 * 用例分类 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class CaseInfoGroupService extends ServiceImpl<CaseInfoGroupMapper, CaseInfoGroup> implements IService<CaseInfoGroup> {

}
