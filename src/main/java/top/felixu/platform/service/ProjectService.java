package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ProjectMapper;
import top.felixu.platform.model.entity.Project;
import org.springframework.stereotype.Service;

/**
 * 项目信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> implements IService<Project> {

}
