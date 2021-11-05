package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.ContactorDTO;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.ContactorGroup;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.CaseInfoService;
import top.felixu.platform.service.ContactorGroupService;
import top.felixu.platform.service.ContactorService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.03
 */
@Service
@RequiredArgsConstructor
public class ContactorManager {

    private final ContactorService contactorService;

    private final ContactorGroupService contactorGroupService;

    private final CaseInfoService caseInfoService;

    public Contactor getContactorById(Integer id) {
        return contactorService.getContactorByIdAndCheck(id);
    }

    public IPage<ContactorDTO> page(Contactor contactor, PageRequestForm form) {
        Page<Contactor> page = contactorService.page(form.toPage(), Wrappers.lambdaQuery(contactor));
        Map<Integer, String> groupMap = contactorGroupService.getContactGroupList().stream()
                .collect(Collectors.toMap(ContactorGroup::getId, ContactorGroup::getName));
        return page.convert(item -> {
            ContactorDTO dto = BeanUtils.map(item, ContactorDTO.class);
            dto.setGroupName(groupMap.get(dto.getGroupId()));
            return dto;
        });
    }

    public Contactor create(Contactor contactor) {
        // 校验分组是否存在
        contactorGroupService.getContactGroupByIdAndCheck(contactor.getGroupId());
        check(contactor);
        return contactorService.create(contactor);
    }

    public Contactor update(Contactor contactor) {
        Contactor original = contactorService.getContactorByIdAndCheck(contactor.getId());
        BeanUtils.copyNotEmpty(Contactor.class, contactor, Contactor.class, original);
        // 校验分组是否存在
        contactorGroupService.getContactGroupByIdAndCheck(original.getGroupId());
        check(original);
        return contactorService.update(original);
    }

    public void delete(Integer id) {
        Contactor contactor = contactorService.getContactorByIdAndCheck(id);
        if (caseInfoService.countByContactorId(id) > 0)
            throw new PlatformException(ErrorCode.CONTACTOR_USED_BY_CASE);
        contactorService.delete(contactor);
    }

    private void check(Contactor contactor) {
        // 校验名称
        Contactor name = contactorService.getOne(Wrappers.<Contactor>lambdaQuery().eq(Contactor::getName, contactor.getName()));
        if (name != null && (contactor.getId() == null || !contactor.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.CONTACTOR_DUPLICATE_NAME);
        // 校验邮箱
        Contactor email = contactorService.getOne(Wrappers.<Contactor>lambdaQuery().eq(Contactor::getEmail, contactor.getEmail()));
        if (email != null && (contactor.getId() == null || !contactor.getId().equals(email.getId())))
            throw new PlatformException(ErrorCode.CONTACTOR_DUPLICATE_EMAIL);
        // 校验手机号
        Contactor phone = contactorService.getOne(Wrappers.<Contactor>lambdaQuery().eq(Contactor::getPhone, contactor.getPhone()));
        if (phone != null && (contactor.getId() == null || !contactor.getId().equals(phone.getId())))
            throw new PlatformException(ErrorCode.CONTACTOR_DUPLICATE_PHONE);
    }
}