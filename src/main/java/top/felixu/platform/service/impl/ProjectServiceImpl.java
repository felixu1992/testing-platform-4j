package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ProjectMapper;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

}
