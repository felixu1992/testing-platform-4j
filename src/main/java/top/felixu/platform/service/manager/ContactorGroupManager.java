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
import top.felixu.platform.model.form.PageRequestForm;
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
public class ContactorGroupManager {

    private final ContactorGroupService contactorGroupService;

    private final ContactorService contactorService;

    public ContactorGroup getContactorGroupById(Integer id) {
        return contactorGroupService.getContactGroupByIdAndCheck(id);
    }

    public IPage<ContactorGroup> page(ContactorGroup group, PageRequestForm form) {
        return contactorGroupService.page(form.toPage(), Wrappers.lambdaQuery(group));
    }

    public List<TreeNodeDTO> tree() {
        List<ContactorGroup> groups = contactorGroupService.getContactGroupList();
        if (CollectionUtils.isEmpty(groups))
            return Collections.emptyList();
        Set<Integer> groupIds = new HashSet<>();
        List<TreeNodeDTO> result = groups.stream().map(group -> {
            groupIds.add(group.getId());
            TreeNodeDTO dto = new TreeNodeDTO(group.getName(), group.getId(), group.getId());
            dto.setDisabled(Boolean.TRUE);
            return dto;
        }).collect(Collectors.toList());
        Map<Integer, List<Contactor>> childrenMap = contactorService.mapByGroupIds(groupIds);
        result.parallelStream().forEach(group -> {
            // ??????????????? Map ???????????????????????? null ???????????? ContactorService#mapByGroupIds ?????????????????? null
            group.setChildren(childrenMap.get(group.getKey()).stream().map(contactor -> new TreeNodeDTO(contactor.getName(), contactor.getId(), contactor.getId())).collect(Collectors.toList()));
        });
        return result.parallelStream()
                .filter(group -> !CollectionUtils.isEmpty(group.getChildren()))
                .collect(Collectors.toList());
    }

    public ContactorGroup create(ContactorGroup group) {
        check(group);
        return contactorGroupService.create(group);
    }

    public ContactorGroup update(ContactorGroup group) {
        ContactorGroup original = contactorGroupService.getContactGroupByIdAndCheck(group.getId());
        BeanUtils.copyNotEmpty(ContactorGroup.class, group, ContactorGroup.class, original);
        check(original);
        return contactorGroupService.update(original);
    }

    public void delete(Integer id) {
        ContactorGroup group = contactorGroupService.getContactGroupByIdAndCheck(id);
        if (contactorService.countByGroupId(id) > 0)
            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_USED_BY_CONTACTOR);
        contactorGroupService.delete(group);
    }

    private void check(ContactorGroup group) {
        ContactorGroup one = contactorGroupService.getOne(Wrappers.<ContactorGroup>lambdaQuery().eq(ContactorGroup::getName, group.getName()));
        if (one != null && (group.getId() == null || !group.getId().equals(one.getId())))
            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_DUPLICATE_NAME);
    }
}
