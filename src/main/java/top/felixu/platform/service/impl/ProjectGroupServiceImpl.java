package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ProjectGroupMapper;
import top.felixu.platform.model.entity.ProjectGroup;
import top.felixu.platform.service.ProjectGroupService;
import org.springframework.stereotype.Service;

/**
 * 项目分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectGroupServiceImpl extends ServiceImpl<ProjectGroupMapper, ProjectGroup> implements ProjectGroupService {

}
