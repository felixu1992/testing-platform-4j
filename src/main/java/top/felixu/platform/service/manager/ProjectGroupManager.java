package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.ContactorGroup;
import top.felixu.platform.model.entity.ProjectGroup;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ProjectGroupService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.06
 */
@Service
@RequiredArgsConstructor
public class ProjectGroupManager {

    private final ProjectGroupService projectGroupService;

    public ProjectGroup getProjectGroupById(Integer id) {
        return projectGroupService.getProjectGroupByIdAndCheck(id);
    }

    public IPage<ProjectGroup> page(ProjectGroup group, PageRequestForm form) {
        return projectGroupService.page(form.toPage(), Wrappers.lambdaQuery(group));
    }

    public List<TreeNodeDTO> tree() {
        List<ProjectGroup> groups = projectGroupService.getProjectGroupList();
        if (CollectionUtils.isEmpty(groups))
            return Collections.emptyList();
        Set<Integer> groupIds = new HashSet<>();
        List<TreeNodeDTO> result = groups.stream().map(group -> {
            groupIds.add(group.getId());
            TreeNodeDTO dto = new TreeNodeDTO(group.getName(), group.getId(), group.getId());
            dto.setDisable(Boolean.TRUE);
            return dto;
        }).collect(Collectors.toList());
        // TODO: 09/06 填充子节点
//        Map<Integer, List<Project>> childrenMap = contactorService.mapByGroupIds(groupIds);
//        result.parallelStream().forEach(group -> {
//            // 没有判断从 Map 中取值的结果不为 null 的原因是 ContactorService#mapByGroupIds 保证了不会为 null
//            group.setChildren(childrenMap.get(group.getKey()).stream().map(contactor -> new TreeNodeDTO(contactor.getName(), contactor.getId(), contactor.getId())).collect(Collectors.toList()));
//        });
        return result;
    }

    public ProjectGroup create(ProjectGroup group) {
        check(group);
        return projectGroupService.create(group);
    }

    public ProjectGroup update(ProjectGroup group) {
        ProjectGroup original = projectGroupService.getProjectGroupByIdAndCheck(group.getId());
        BeanUtils.copyNotEmpty(ProjectGroup.class, group, ProjectGroup.class, original);
        check(original);
        return projectGroupService.update(original);
    }

    public void delete(Integer id) {
        ProjectGroup group = projectGroupService.getProjectGroupByIdAndCheck(id);
//        if (contactorService.countByGroupId(id) > 0)
//            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_USED_BY_CONTACTOR);
        projectGroupService.delete(group);
    }

    private void check(ProjectGroup group) {
        ProjectGroup one = projectGroupService.getOne(Wrappers.<ProjectGroup>lambdaQuery().eq(ProjectGroup::getName, group.getName()));
        if (one != null && (group.getId() == null || !group.getId().equals(one.getId())))
            throw new PlatformException(ErrorCode.PROJECT_GROUP_DUPLICATE_NAME);
    }
}