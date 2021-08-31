package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.ContactorTreeDTO;
import top.felixu.platform.model.entity.ContactorGroup;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ContactorGroupService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    public ContactorGroup getContactorGroupById(Integer id) {
        return contactorGroupService.getContactGroupByIdAndCheck(id);
    }

    public IPage<ContactorGroup> page(ContactorGroup group, PageRequestForm form) {
        return contactorGroupService.page(form.toPage(), Wrappers.lambdaQuery(group));
    }

    public List<ContactorTreeDTO> tree() {
        List<ContactorGroup> groups = contactorGroupService.getContactGroupList();
        if (CollectionUtils.isEmpty(groups))
            return Collections.emptyList();
        Set<Integer> groupIds = new HashSet<>();
        List<ContactorTreeDTO> result = groups.stream().map(group -> {
            groupIds.add(group.getId());
            ContactorTreeDTO dto = new ContactorTreeDTO();
            dto.setTitle(group.getName());
            dto.setValue(group.getId());
            dto.setKey(group.getId());
            dto.setDisable(Boolean.TRUE);
            return dto;
        }).collect(Collectors.toList());
        // TODO: 08/31 填充子节点
        return result;
    }

    public ContactorGroup create(ContactorGroup group) {
        check(group);
        return contactorGroupService.create(group);
    }

    public ContactorGroup update(ContactorGroup group) {
        check(group);
        return contactorGroupService.update(group);
    }

    public void delete(Integer id) {
        ContactorGroup group = contactorGroupService.getContactGroupByIdAndCheck(id);
        // TODO: 08/31 检查是否被联系人使用
        contactorGroupService.delete(group);
    }

    private void check(ContactorGroup group) {
        ContactorGroup one = contactorGroupService.getOne(Wrappers.<ContactorGroup>lambdaQuery().eq(ContactorGroup::getName, group.getName()));
        if (one != null && (group.getId() == null || !group.getId().equals(one.getId())))
            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_DUPLICATE_NAME);
    }
}
