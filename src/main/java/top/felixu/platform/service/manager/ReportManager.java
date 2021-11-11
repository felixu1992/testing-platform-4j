package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.model.dto.ReportDTO;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.ProjectGroup;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ProjectGroupService;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.ReportService;
import top.felixu.platform.service.UserProjectService;
import top.felixu.platform.util.WrapperUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.11.10
 */
@Service
@RequiredArgsConstructor
public class ReportManager {

    private final ReportService reportService;

    private final ProjectService projectService;

    private final ProjectGroupService projectGroupService;

    private final UserProjectService userProjectService;

    public IPage<ReportDTO> page(Report report, PageRequestForm form) {
        // 分页查询
        Page<Report> page = reportService.page(form.toPage(), WrapperUtils.relation(Wrappers.lambdaQuery(report),
                Report::getProjectId, userProjectService));
        // 查询分组
        Map<Integer, String> groupMap = projectGroupService.getProjectGroupList()
                .parallelStream()
                .collect(Collectors.toMap(ProjectGroup::getId, ProjectGroup::getName));
        Set<Integer> projectIds = page.getRecords()
                .parallelStream()
                .map(Report::getProjectId)
                .collect(Collectors.toSet());
        Map<Integer, String> projectMap = projectService.listByProjectIds(new ArrayList<>(projectIds))
                .parallelStream()
                .collect(Collectors.toMap(Project::getId, Project::getName));
        return page.convert(item -> {
            ReportDTO dto = BeanUtils.map(item, ReportDTO.class);
            dto.setGroupName(groupMap.get(dto.getGroupId()));
            dto.setProjectName(projectMap.get(dto.getProjectId()));
            return dto;
        });
    }
}
