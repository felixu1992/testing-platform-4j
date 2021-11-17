package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.constants.DefaultConstants;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.ProjectDTO;
import top.felixu.platform.model.dto.StatisticsDTO;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.CaseInfoGroup;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.ProjectContactor;
import top.felixu.platform.model.entity.ProjectGroup;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.form.ProjectCopyForm;
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.CaseInfoService;
import top.felixu.platform.service.ProjectContactorService;
import top.felixu.platform.service.ProjectGroupService;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.ReportService;
import top.felixu.platform.service.UserProjectService;
import top.felixu.platform.util.UserHolderUtils;
import top.felixu.platform.util.WrapperUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final UserProjectService userProjectService;

    private final ReportService reportService;

    public Project getProjectById(Integer id) {
        return projectService.getProjectByIdAndCheck(id);
    }

    public IPage<ProjectDTO> page(Project project, PageRequestForm form) {
        Page<Project> page = projectService.page(form.toPage(), Wrappers.lambdaQuery(project));
        Map<Integer, String> groupMap = projectGroupService.getProjectGroupList().stream()
                .collect(Collectors.toMap(ProjectGroup::getId, ProjectGroup::getName));
        return page.convert(item -> {
            ProjectDTO dto = BeanUtils.map(item, ProjectDTO.class);
            dto.setGroupName(groupMap.get(dto.getGroupId()));
            return dto;
        });
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

    public StatisticsDTO statistics() {
        StatisticsDTO result = new StatisticsDTO(0, 0, 0);
        boolean condition = UserHolderUtils.getCurrentRole() == RoleTypeEnum.ORDINARY;
        List<Integer> projectIds = condition ? userProjectService.getProjectIdsByUserId(UserHolderUtils.getCurrentUserId()) : new ArrayList<>();
        result.setProjectNum(projectService.count(WrapperUtils.relation(Wrappers.<Project>lambdaQuery(), Project::getId, projectIds, condition)));
        result.setCaseNum(caseInfoService.count(WrapperUtils.relation(Wrappers.<CaseInfo>lambdaQuery(), CaseInfo::getProjectId, projectIds, condition)));
        result.setRecordNum(reportService.count(WrapperUtils.relation(Wrappers.<Report>lambdaQuery(), Report::getProjectId, projectIds, condition)));
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

    /**
     * <p>项目导出（推流方式）</p>
     *
     *
     * @param: id
     *         项目ID
     * @param: response
     *         请求响应
     * @return: java.io.OutputStream
     * @author zhan9yn
     * @date: 2021/11/7 11:59
     */
    public void export(String id, HttpServletResponse response) {
        //查找该项目的数据
//        Map<Integer, List<CaseInfo>> map = new HashMap<>();
//        List<CaseInfo> caseInfoList = caseInfoService.list(Wrappers.lambdaQuery(new CaseInfo()).eq(CaseInfo::getProjectId, id).select());
//        caseInfoList.parallelStream().forEach(ca3e -> {
//            List<CaseInfo> list = map.get(ca3e.getGroupId());
//            if (list == null || list.isEmpty()) {
//                list = new ArrayList<>(16);
//            }
//            list.add(ca3e);
//        });
//        List<ProjectPackageDTO> packageList = caseInfoGroupService
//                .list(Wrappers.lambdaQuery(new CaseInfoGroup()).eq(CaseInfoGroup::getProjectId, id).select())
//                .parallelStream().map(group -> new ProjectPackageDTO(group, map.get(group.getId())))
//                .collect(Collectors.toList());
//        //写成json文件
//        String filePath = "file/";
//        String fileName = LocalDateTime.now().toString() + "-" + id + ".json";
//        File jsonFile = new File(filePath + fileName);
//        try {
//            FileOutputStream fos = new FileOutputStream(jsonFile);
//            fos.write(JSON.toJSONString(packageList).getBytes(Charset.forName("UTF-8")));
//            //打成zip包
//            FileUtils.zipFile("file/", fileName, "file/");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //zip包推流
    }

    public void importV1 (MultipartFile file) {

    }
}
