package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.entity.CaseInfoGroup;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.ContactorGroup;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.ContactorGroupService;
import top.felixu.platform.service.ContactorService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.08.31
 */
@Service
@RequiredArgsConstructor
public class CaseInfoGroupManager {

    private final CaseInfoGroupService caseInfoGroupService;

    public CaseInfoGroup getCaseInfoGroupById(Integer id) {
        return caseInfoGroupService.getCaseInfoGroupByIdAndCheck(id);
    }

    public IPage<CaseInfoGroup> page(CaseInfoGroup group, PageRequestForm form) {
        return caseInfoGroupService.page(form.toPage(), Wrappers.lambdaQuery(group));
    }

    public List<TreeNodeDTO> tree(Integer projectId) {
        List<CaseInfoGroup> groups = caseInfoGroupService.getCaseInfoGroupList();
        if (CollectionUtils.isEmpty(groups))
            return Collections.emptyList();
        Set<Integer> groupIds = new HashSet<>();
        List<TreeNodeDTO> result = groups.stream().map(group -> {
            groupIds.add(group.getId());
            TreeNodeDTO dto = new TreeNodeDTO(group.getName(), group.getId(), group.getId());
            dto.setDisable(Boolean.TRUE);
            return dto;
        }).collect(Collectors.toList());
        // TODO: 09/07 填充子节点，并且这个接口是否需要，存疑
//        Map<Integer, List<Contactor>> childrenMap = contactorService.mapByGroupIds(groupIds);
//        result.parallelStream().forEach(group -> {
//            // 没有判断从 Map 中取值的结果不为 null 的原因是 ContactorService#mapByGroupIds 保证了不会为 null
//            group.setChildren(childrenMap.get(group.getKey()).stream().map(contactor -> new TreeNodeDTO(contactor.getName(), contactor.getId(), contactor.getId())).collect(Collectors.toList()));
//        });
        return result;
    }

    public CaseInfoGroup create(CaseInfoGroup group) {
        check(group);
        return caseInfoGroupService.create(group);
    }

    public CaseInfoGroup update(CaseInfoGroup group) {
        CaseInfoGroup original = caseInfoGroupService.getCaseInfoGroupByIdAndCheck(group.getId());
        BeanUtils.copyNotEmpty(CaseInfoGroup.class, group, CaseInfoGroup.class, original);
        check(original);
        return caseInfoGroupService.update(original);
    }

    public void delete(Integer id) {
        CaseInfoGroup group = caseInfoGroupService.getCaseInfoGroupByIdAndCheck(id);
        // TODO: 09/07 判断是否有用例使用
//        if (contactorService.countByGroupId(id) > 0)
//            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_USED_BY_CONTACTOR);
        caseInfoGroupService.delete(group);
    }

    private void check(CaseInfoGroup group) {
        CaseInfoGroup one = caseInfoGroupService.getOne(Wrappers.<CaseInfoGroup>lambdaQuery().eq(CaseInfoGroup::getName, group.getName()));
        if (one != null && (group.getId() == null || !group.getId().equals(one.getId())))
            throw new PlatformException(ErrorCode.CASE_GROUP_DUPLICATE_NAME);
    }
}
