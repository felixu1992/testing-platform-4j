package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.model.dto.RecordDTO;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.ProjectGroup;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ProjectGroupService;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.RecordService;
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
public class RecordManager {

    private final RecordService recordService;

    private final ProjectService projectService;

    private final ProjectGroupService projectGroupService;

    private final UserProjectService userProjectService;

    public IPage<RecordDTO> page(Record record, PageRequestForm form) {
        // 分页查询
        Page<Record> page = recordService.page(form.toPage(), WrapperUtils.relation(Wrappers.lambdaQuery(record),
                Record::getProjectId, userProjectService));
        // 查询分组
        Map<Integer, String> groupMap = projectGroupService.getProjectGroupList()
                .parallelStream()
                .collect(Collectors.toMap(ProjectGroup::getId, ProjectGroup::getName));
        Set<Integer> projectIds = page.getRecords()
                .parallelStream()
                .map(Record::getProjectId)
                .collect(Collectors.toSet());
        Map<Integer, String> projectMap = projectService.listByProjectIds(new ArrayList<>(projectIds))
                .parallelStream()
                .collect(Collectors.toMap(Project::getId, Project::getName));
        return page.convert(item -> {
            RecordDTO dto = BeanUtils.map(item, RecordDTO.class);
            dto.setGroupName(groupMap.get(dto.getGroupId()));
            dto.setProjectName(projectMap.get(dto.getProjectId()));
            return dto;
        });
    }
}
