package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.CaseInfoGroupMapper;
import top.felixu.platform.model.entity.CaseInfoGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static top.felixu.platform.constants.CacheKeyConstants.CaseInfoGroup.CASE_GROUP_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.CaseInfoGroup.CASE_GROUP_LIST_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.CaseInfoGroup.NAME;

/**
 * 用例分类 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class CaseInfoGroupService extends ServiceImpl<CaseInfoGroupMapper, CaseInfoGroup> implements IService<CaseInfoGroup> {

    @Cacheable(cacheNames = NAME, key = CASE_GROUP_CACHE + " + #id", unless = "#result == null", sync = true)
    public CaseInfoGroup getCaseInfoGroupByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.CONTACTOR_GROUP_NOT_FOUND));
    }

    @Cacheable(cacheNames = NAME, key = CASE_GROUP_LIST_CACHE + " + T(top.felixu.platform.util.UserHolderUtils).getOwner()")
    public List<CaseInfoGroup> getCaseInfoGroupList() {
        return list();
    }

    @Caching(
            cacheable = @Cacheable(cacheNames = NAME, key = CASE_GROUP_CACHE + " + #result.getId()", unless = "#result == null", sync = true),
            evict = @CacheEvict(cacheNames = NAME, key = CASE_GROUP_LIST_CACHE + " + #result.getOwner()")
    )
    public CaseInfoGroup create(CaseInfoGroup group) {
        save(group);
        return group;
    }

    @Caching(
            put = @CachePut(cacheNames = NAME, key = CASE_GROUP_CACHE + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = CASE_GROUP_LIST_CACHE + " + #result.getOwner()")
    )
    public CaseInfoGroup update(CaseInfoGroup group) {
        updateById(group);
        return group;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = NAME, key = CASE_GROUP_LIST_CACHE + " + #group.getOwner()"),
                    @CacheEvict(cacheNames = NAME, key = CASE_GROUP_CACHE + " + #group.getId()")
            }
    )
    public void delete(CaseInfoGroup group) {
        removeById(group.getId());
    }
}
