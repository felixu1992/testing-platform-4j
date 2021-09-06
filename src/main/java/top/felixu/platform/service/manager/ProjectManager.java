package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.ProjectContactor;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ProjectContactorService;
import top.felixu.platform.service.ProjectGroupService;
import top.felixu.platform.service.ProjectService;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.06
 */
@Service
@RequiredArgsConstructor
public class ProjectManager {

    private final ProjectService projectService;

    private final ProjectGroupService projectGroupService;

    private final ProjectContactorService projectContactorService;

    public Project getProjectById(Integer id) {
        return projectService.getProjectByIdAndCheck(id);
    }

    public IPage<Project> page(Project project, PageRequestForm form) {
        return projectService.page(form.toPage(), Wrappers.lambdaQuery(project));
    }

    public Project create(Project project) {
        // 校验分组是否存在
        projectGroupService.getProjectGroupByIdAndCheck(project.getGroupId());
        check(project);
        Project result = projectService.create(project);
        savaProjectContactorRelations(project, result.getId());
        return result;
    }

    public Project update(Project project) {
        Project original = projectService.getProjectByIdAndCheck(project.getId());
        BeanUtils.copyNotEmpty(Project.class, project, Project.class, original);
        // 校验分组是否存在
        projectGroupService.getProjectGroupByIdAndCheck(original.getGroupId());
        check(original);
        savaProjectContactorRelations(project, project.getId());
        return projectService.update(original);
    }

    public void delete(Integer id) {
        Project project = projectService.getProjectByIdAndCheck(id);
        // TODO: 09/06 判断是否有用例
        projectService.delete(project);
    }

    private void savaProjectContactorRelations(Project project, Integer id) {
        projectContactorService.update(CollectionUtils.isEmpty(project.getContactorIds()) ? Collections.emptyList() :
                project.getContactorIds().stream().map(contactorId -> {
                    ProjectContactor relation = new ProjectContactor();
                    relation.setProjectId(id);
                    relation.setContactorId(contactorId);
                    return relation;
                }).collect(Collectors.toList())
        );
    }

    private void check(Project project) {
        // 校验名称
        Project name = projectService.getOne(Wrappers.<Project>lambdaQuery().eq(Project::getName, project.getName()));
        if (name != null && (project.getId() == null || !project.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.PROJECT_DUPLICATE_NAME);
    }
}
