package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.ContactorGroupMapper;
import top.felixu.platform.model.entity.ContactorGroup;
import org.springframework.stereotype.Service;

/**
 * 联系人分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ContactorGroupService extends ServiceImpl<ContactorGroupMapper, ContactorGroup> implements IService<ContactorGroup> {

    @Cacheable(cacheNames = "USER", key = "'USER-CACHE-' + #id", unless = "#result == null", sync = true)
    public ContactorGroup getContactGroupByIdAndCheck(Integer id) {
        ContactorGroup group = getById(id);
        if (null == group)
            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_NOT_FOUND);
        return group;
    }
}