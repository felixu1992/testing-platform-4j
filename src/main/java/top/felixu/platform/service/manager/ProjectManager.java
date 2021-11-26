package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.felixu.common.bean.BeanUtils;
import top.felixu.common.json.JsonUtils;
import top.felixu.platform.constants.DefaultConstants;
import top.felixu.platform.enums.HttpMethodEnum;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.ProjectDTO;
import top.felixu.platform.model.dto.StatisticsDTO;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.CaseInfoGroup;
import top.felixu.platform.model.entity.Dependency;
import top.felixu.platform.model.entity.Expected;
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
import top.felixu.platform.util.ApplicationUtils;
import top.felixu.platform.util.UserHolderUtils;
import top.felixu.platform.util.ValueUtils;
import top.felixu.platform.util.WrapperUtils;
import top.felixu.platform.util.excel.ExcelReader;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.06
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
        // 校验权限
        userProjectService.checkAuthority(project.getId());
        Project original = projectService.getProjectByIdAndCheck(project.getId());
        BeanUtils.copyNotEmpty(Project.class, project, Project.class, original);
        // 校验分组是否存在
        projectGroupService.getProjectGroupByIdAndCheck(original.getGroupId());
        check(original);
        savaProjectContactorRelations(project.getContactorIds(), project.getId());
        return projectService.update(original);
    }

    public void delete(Integer id) {
        // 校验权限
        userProjectService.checkAuthority(id);
        Project project = projectService.getProjectByIdAndCheck(id);
        if (caseInfoService.countByProjectId(id) > 0)
            throw new PlatformException(ErrorCode.PROJECT_USED_BY_CASE);
        projectService.delete(project);
    }

    public Project copy(ProjectCopyForm form) {
        // 校验权限
        userProjectService.checkAuthority(form.getId());
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

    @Transactional(rollbackFor = Exception.class)
    public void importV1 (Integer id, MultipartFile file) {
        // TODO: 11/25 和新增用例，使用分布式锁保证数据完整性
        // 权限校验
        userProjectService.checkAuthority(id);
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Iterator<Sheet> sheets = wb.sheetIterator();
            while (sheets.hasNext())
                doImportV1(id, parseExcelV1(sheets.next()));
        } catch (IOException ex) {
            // TODO: 11/25 exception
        }
    }

    private void doImportV1(Integer projectId, List<CaseInfo> caseInfos) {
        // 所有用例均放到默认分组下，自己处理
        CaseInfoGroup defaultGroup = caseInfoGroupService.getDefaultCaseInfoGroup(projectId);
        List<CaseInfo> noDependencies = new ArrayList<>();
        List<CaseInfo> haveDependencies = new ArrayList<>();
        // 拆分成两个数组分别处理
        caseInfos.forEach(caseInfo -> {
            caseInfo.setProjectId(projectId);
            caseInfo.setGroupId(defaultGroup.getId());
            if (CollectionUtils.isEmpty(caseInfo.getDependencies()))
                noDependencies.add(caseInfo);
            else
                haveDependencies.add(caseInfo);
        });
        // 懒得琢磨批量的事情了，就这样吧，拉倒
        CaseInfoManager caseInfoManager = ApplicationUtils.getBean(CaseInfoManager.class);
        noDependencies.forEach(caseInfoManager::create);
        System.out.println("-----------");
        haveDependencies.forEach(caseInfo -> {
            caseInfo.getDependencies().forEach(dept ->
                    dept.getDependValue().setDepend(caseInfos.get(dept.getDependValue().getDepend()).getId()));
            caseInfoManager.create(caseInfo);
        });
        // TODO 还有一种情况是结果依赖于用例次
    }

    @SneakyThrows
    private List<CaseInfo> parseExcelV1(Sheet sheet) {
        return ExcelReader.readFormSheet(sheet, 5, CaseInfo::new,
                // 0. 备注
                cell -> cell.getData().setRemark(cell.getNullableString()),
                // 1. 名称
                cell -> cell.getData().setName(cell.getRequiredString()),
                // 2. 是否运行
                cell -> cell.getData().setRun(ValueUtils.emptyAs("NO".equalsIgnoreCase(cell.getNullableString()), true)),
                // 3. 请求方法
                cell -> cell.getData().setMethod(HttpMethodEnum.valueOf(cell.getRequiredString().toUpperCase())),
                // 4. 请求地址
                cell -> cell.getData().setHost(cell.getNullableString()),
                // 5. 请求路径
                cell -> cell.getData().setPath(cell.getRequiredString()),
                // 6. 延迟执行
                cell -> cell.getData().setDelay(ValueUtils.emptyAs(cell.getNullableInteger(), 0)),
                // 7. 请求参数
                cell -> cell.getData().setParams(JsonUtils.fromJsonToMap(ValueUtils.emptyAs(cell.getNullableString(), "{}"))),
                // 8. 动态参数，如果没有值，给个空集合，否则转为依赖对象列表
                cell -> cell.getData().setDependencies(StringUtils.hasText(cell.getNullableString()) ?
                        Arrays.stream(cell.getNullableString().split(",")).map(exKey -> {
                            Dependency dependency = new Dependency();
                            dependency.setDependKey(exKey.split("\\."));
                            return dependency;
                        }).collect(Collectors.toList())
                        : new ArrayList<>()),
                // 9. 动态参数补充依赖过程
                cell -> {
                    List<Dependency> dependencies = cell.getData().getDependencies();
                    if (!CollectionUtils.isEmpty(dependencies)) {
                        String stepStr = cell.getRequiredString();
                        String[] stepStrArray = stepStr.split(",");
                        for (int i = 0; i < dependencies.size(); i++) {
                            Dependency dependency = dependencies.get(i);
                            String[] split = stepStrArray[i].split(":");
                            Dependency.DependValue dependValue = new Dependency.DependValue();
                            dependValue.setDepend(Integer.parseInt(split[0]) - 5);
                            dependValue.setSteps(split[1].split("\\."));
                            dependency.setDependValue(dependValue);
                        }
                    }
                },
                // 10. 请求头
                cell -> cell.getData().setHeaders(JsonUtils.fromJsonToMap(ValueUtils.emptyAs(cell.getNullableString(), "{}"), String.class, String.class)),
                // 11. 校验预期
                cell -> cell.getData().setExpects(new ArrayList<>()),
                // 12. 校验预期补充校验值
                cell -> cell.getData().setExpects(Arrays.stream(cell.getRequiredString().split(","))
                        .map(valStr -> {
                            Expected expected = new Expected();
                            Expected.ExpectValue expectValue = new Expected.ExpectValue();
                            String[] split = valStr.split(":");
                            if (split.length == 1) {
                                expectValue.setFixed(true);
                                expectValue.setValue(valStr);
                            } else {
                                expectValue.setDepend(Integer.parseInt(split[0]) - 5);
                                expectValue.setSteps(split[1].split("\\."));
                            }
                            expected.setExpectValue(expectValue);
                            return expected;
                        })
                        .collect(Collectors.toList())),
                // 13. 校验预期补充校验过程
                cell -> {
                    String[] expectKeys = cell.getRequiredString().split(",");
                    for (int i = 0; i < cell.getData().getExpects().size(); i++) {
                        Expected expected = cell.getData().getExpects().get(i);
                        expected.setExpectKey(expectKeys[i].split("\\."));
                    }
                });
    }
}
