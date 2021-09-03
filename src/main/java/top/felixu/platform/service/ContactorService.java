package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
import top.felixu.platform.constants.CacheKeyConstants;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.ContactorMapper;
import top.felixu.platform.model.entity.Contactor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static top.felixu.platform.constants.CacheKeyConstants.Contactor.CONTACTOR_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.Contactor.NAME;

/**
 * 联系人 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ContactorService extends ServiceImpl<ContactorMapper, Contactor> implements IService<Contactor> {

    @Cacheable(cacheNames = NAME, key = CONTACTOR_CACHE + " + #id", unless = "#result == null", sync = true)
    public Contactor getContactorByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.CONTACTOR_NOT_FOUND));
    }

    public int countByGroupId(Integer groupId) {
        return count(Wrappers.<Contactor>lambdaQuery().eq(Contactor::getGroupId, groupId));
    }

    public List<Contactor> listByGroupId(Integer groupId) {
        return list(Wrappers.<Contactor>lambdaQuery().eq(Contactor::getGroupId, groupId));
    }

    public Map<Integer, List<Contactor>> mapByGroupIds(Set<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds))
            return Collections.emptyMap();
        Map<Integer, List<Contactor>> collect = list(Wrappers.<Contactor>lambdaQuery().in(Contactor::getGroupId, groupIds)).stream()
                .collect(Collectors.groupingBy(Contactor::getGroupId));
        Map<Integer, List<Contactor>> result = new HashMap<>(groupIds.size());
        groupIds.forEach(groupId -> result.put(groupId, Optional.ofNullable(collect.get(groupId)).orElse(Collections.emptyList())));
        return result;
    }

    @Cacheable(cacheNames = NAME, key = CONTACTOR_CACHE + " + #result.getId()", unless = "#result == null", sync = true)
    public Contactor create(Contactor contactor) {
        save(contactor);
        return contactor;
    }

    @CachePut(cacheNames = NAME, key = CONTACTOR_CACHE + " + #result.getId()", unless = "#result == null")
    public Contactor update(Contactor contactor) {
        updateById(contactor);
        return contactor;
    }

    @CacheEvict(cacheNames = NAME, key = CONTACTOR_CACHE + " + #contactor.getId()")
    public void delete(Contactor contactor) {
        removeById(contactor.getId());
    }
}
