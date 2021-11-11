package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.model.dto.RecordDTO;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.ReportService;
import top.felixu.platform.service.RecordService;
import top.felixu.platform.service.UserProjectService;

/**
 * @author felixu
 * @since 2021.11.11
 */
@Service
@RequiredArgsConstructor
public class RecordManager {

    private final RecordService recordService;

    private final ReportService reportService;

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    public IPage<RecordDTO> page(Record record, PageRequestForm form) {
        // 查询所属 Record
        Report report = reportService.getRecordByIdAndCheck(record.getRecordId());
        // 校验是否有此项目的权限
        userProjectService.checkAuthority(report.getProjectId());
        // 查询项目
        Project project = projectService.getProjectByIdAndCheck(report.getProjectId());
        // 分页查询
        return recordService.page(form.toPage(), new QueryWrapper<>(record))
                .convert(item -> {
                    RecordDTO dto = BeanUtils.map(item, RecordDTO.class);
                    dto.setProjectName(project.getName());
                    return dto;
                });
    }
}
