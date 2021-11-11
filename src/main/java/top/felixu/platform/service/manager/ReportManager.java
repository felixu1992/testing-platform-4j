package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.model.dto.ReportDTO;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.RecordService;
import top.felixu.platform.service.ReportService;
import top.felixu.platform.service.UserProjectService;

/**
 * @author felixu
 * @since 2021.11.11
 */
@Service
@RequiredArgsConstructor
public class ReportManager {

    private final ReportService reportService;

    private final RecordService recordService;

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    public IPage<ReportDTO> page(Report report, PageRequestForm form) {
        // 查询所属 Record
        Record record = recordService.getRecordByIdAndCheck(report.getRecordId());
        // 校验是否有此项目的权限
        userProjectService.checkAuthority(record.getProjectId());
        // 查询项目
        Project project = projectService.getProjectByIdAndCheck(record.getProjectId());
        // 分页查询
        return reportService.page(form.toPage(), new QueryWrapper<>(report))
                .convert(item -> {
                    ReportDTO dto = BeanUtils.map(item, ReportDTO.class);
                    dto.setProjectName(project.getName());
                    return dto;
                });
    }
}
