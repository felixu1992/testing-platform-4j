package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.ContactorGroupMapper;
import top.felixu.platform.model.entity.ContactorGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static top.felixu.platform.constants.CacheKeyConstants.ContactorGroup.CONTACTOR_GROUP;
import static top.felixu.platform.constants.CacheKeyConstants.ContactorGroup.CONTACTOR_GROUP_LIST;
import static top.felixu.platform.constants.CacheKeyConstants.ContactorGroup.NAME;

/**
 * 联系人分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ContactorGroupService extends ServiceImpl<ContactorGroupMapper, ContactorGroup> implements IService<ContactorGroup> {

    @Cacheable(cacheNames = NAME, key = CONTACTOR_GROUP + " + #id", unless = "#result == null")
    public ContactorGroup getContactGroupByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.CONTACTOR_GROUP_NOT_FOUND));
    }

    @Cacheable(cacheNames = NAME, key = CONTACTOR_GROUP_LIST + " + T(top.felixu.platform.util.UserHolderUtils).getOwner()")
    public List<ContactorGroup> getContactGroupList() {
        return list();
    }

    @Caching(
            cacheable = @Cacheable(cacheNames = NAME, key = CONTACTOR_GROUP + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = CONTACTOR_GROUP_LIST + " + #result.getOwner()")
    )
    public ContactorGroup create(ContactorGroup group) {
        save(group);
        return group;
    }

    @Caching(
            put = @CachePut(cacheNames = NAME, key = CONTACTOR_GROUP + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = CONTACTOR_GROUP_LIST + " + #result.getOwner()")
    )
    public ContactorGroup update(ContactorGroup group) {
        updateById(group);
        return group;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = NAME, key = CONTACTOR_GROUP_LIST + " + #group.getOwner()"),
                    @CacheEvict(cacheNames = NAME, key = CONTACTOR_GROUP + " + #group.getId()")
            }
    )
    public void delete(ContactorGroup group) {
        removeById(group.getId());
    }
}