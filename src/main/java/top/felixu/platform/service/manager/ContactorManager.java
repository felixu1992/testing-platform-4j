package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.ContactorGroupService;
import top.felixu.platform.service.ContactorService;

/**
 * @author felixu
 * @since 2021.09.03
 */
@Service
@RequiredArgsConstructor
public class ContactorManager {

    private final ContactorService contactorService;

    private final ContactorGroupService contactorGroupService;

    public Contactor getContactorById(Integer id) {
        return contactorService.getContactorByIdAndCheck(id);
    }

    public IPage<Contactor> page(Contactor contactor, PageRequestForm form) {
        return contactorService.page(form.toPage(), Wrappers.lambdaQuery(contactor));
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
        // TODO: 09/03 是否被用例使用(用例、项目)
        contactorService.delete(contactor);
    }

    private void check(Contactor contactor) {
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