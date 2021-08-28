package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.CaseInfoMapper;
import top.felixu.platform.model.entity.CaseInfo;
import org.springframework.stereotype.Service;

/**
 * 用例信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class CaseInfoService extends ServiceImpl<CaseInfoMapper, CaseInfo> implements IService<CaseInfo> {

}
