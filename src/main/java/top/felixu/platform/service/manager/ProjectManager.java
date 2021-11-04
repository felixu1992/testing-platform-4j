package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.constants.DefaultConstants;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.CaseInfoGroup;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.ProjectContactor;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.form.ProjectCopyForm;
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.CaseInfoService;
import top.felixu.platform.service.ProjectContactorService;
import top.felixu.platform.service.ProjectGroupService;
import top.felixu.platform.service.ProjectService;

import java.util.Collections;
import java.util.List;
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

    private final CaseInfoService caseInfoService;

    private final CaseInfoGroupService caseInfoGroupService;

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
        savaProjectContactorRelations(project.getContactorIds(), result.getId());
        // 创建用例默认分类
        CaseInfoGroup group = new CaseInfoGroup();
        group.setName(DefaultConstants.CaseGroup.NAME);
        group.setProjectId(result.getId());
        caseInfoGroupService.create(group);
        return result;
    }

    public Project update(Project project) {
        Project original = projectService.getProjectByIdAndCheck(project.getId());
        BeanUtils.copyNotEmpty(Project.class, project, Project.class, original);
        // 校验分组是否存在
        projectGroupService.getProjectGroupByIdAndCheck(original.getGroupId());
        check(original);
        savaProjectContactorRelations(project.getContactorIds(), project.getId());
        return projectService.update(original);
    }

    public void delete(Integer id) {
        Project project = projectService.getProjectByIdAndCheck(id);
        if (caseInfoService.countByProjectId(id) > 0)
            throw new PlatformException(ErrorCode.PROJECT_USED_BY_CASE);
        projectService.delete(project);
    }

    public Project copy(ProjectCopyForm form) {
        // 拷贝项目
        Project original = projectService.getProjectByIdAndCheck(form.getId());
        Project project = BeanUtils.map(original, Project.class);
        project.setName(form.getName());
        project.setId(null);
        Project result = projectService.create(project);
        // 拷贝联系人
        List<Integer> contactorIds = projectContactorService.getContactorsByProjectId(form.getId());
        savaProjectContactorRelations(contactorIds, result.getId());
        // 拷贝用例
        List<CaseInfo> caseInfos = caseInfoService.listByProjectId(form.getId());
        caseInfos.forEach(caseInfo -> {
            caseInfo.setProjectId(result.getId());
            caseInfo.setGroupId(caseInfoGroupService.getDefaultCaseInfoGroup(result.getId()).getId());
            caseInfo.setId(null);
            caseInfo.setCreatedAt(null);
            caseInfo.setCreatedBy(null);
            caseInfo.setUpdatedAt(null);
            caseInfo.setUpdatedBy(null);
        });
        caseInfoService.saveBatch(caseInfos);
        return result;
    }

    private void savaProjectContactorRelations(List<Integer> contactorIds, Integer id) {
        projectContactorService.update(CollectionUtils.isEmpty(contactorIds) ? Collections.emptyList() :
                contactorIds.stream().map(contactorId -> {
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
