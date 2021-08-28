package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ProjectContactorMapper;
import top.felixu.platform.model.entity.ProjectContactor;
import top.felixu.platform.service.ProjectContactorService;
import org.springframework.stereotype.Service;

/**
 * 项目和需要通知的联系人关联表 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectContactorServiceImpl extends ServiceImpl<ProjectContactorMapper, ProjectContactor> implements ProjectContactorService {

}