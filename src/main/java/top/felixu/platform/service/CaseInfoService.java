package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.CaseInfoMapper;
import top.felixu.platform.model.entity.CaseInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static top.felixu.platform.constants.CacheKeyConstants.CaseInfo.CASE_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.CaseInfo.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.CaseInfo.PROJECT_CASE_CACHE;

/**
 * 用例信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class CaseInfoService extends ServiceImpl<CaseInfoMapper, CaseInfo> implements IService<CaseInfo> {

    @Cacheable(cacheNames = NAME, key = CASE_CACHE + " + #id", unless = "#result == null", sync = true)
    public CaseInfo getCaseInfoByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.CASE_NOT_FOUND));
    }

    public int countByGroupId(Integer groupId) {
        return count(Wrappers.<CaseInfo>lambdaQuery().eq(CaseInfo::getGroupId, groupId));
    }

    public int countByContactorId(Integer contactorId) {
        return count(Wrappers.<CaseInfo>lambdaQuery().eq(CaseInfo::getDeveloper, contactorId));
    }

    public int countByProjectId(Integer projectId) {
        return count(Wrappers.<CaseInfo>lambdaQuery().eq(CaseInfo::getProjectId, projectId));
    }

    public List<CaseInfo> listByGroupId(@NonNull Integer projectId, Integer groupId) {
        return list(Wrappers.<CaseInfo>lambdaQuery().eq(CaseInfo::getProjectId, projectId)
                .eq(CaseInfo::getGroupId, groupId));
    }

    @Cacheable(cacheNames = NAME, key = PROJECT_CASE_CACHE + " + #projectId", unless = "#result == null", sync = true)
    public Map<Integer, List<CaseInfo>> mapByGroupIds(@NonNull Integer projectId, Set<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds))
            return Collections.emptyMap();
        Map<Integer, List<CaseInfo>> collect = list(Wrappers.<CaseInfo>lambdaQuery().eq(CaseInfo::getProjectId, projectId)
                .in(CaseInfo::getGroupId, groupIds)).stream()
                .collect(Collectors.groupingBy(CaseInfo::getGroupId));
        Map<Integer, List<CaseInfo>> result = new HashMap<>(groupIds.size());
        groupIds.forEach(groupId -> result.put(groupId, Optional.ofNullable(collect.get(groupId)).orElse(Collections.emptyList())));
        return result;
    }

    @Caching(
            cacheable = @Cacheable(cacheNames = NAME, key = CASE_CACHE + " + #result.getId()", unless = "#result == null", sync = true),
            evict = @CacheEvict(cacheNames = NAME, key = PROJECT_CASE_CACHE + " + #result.getProjectId()")
    )
    public CaseInfo create(CaseInfo caseInfo) {
        save(caseInfo);
        return caseInfo;
    }

    @Caching(
            put = @CachePut(cacheNames = NAME, key = CASE_CACHE + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = PROJECT_CASE_CACHE + " + #result.getProjectId()")
    )
    public CaseInfo update(CaseInfo caseInfo) {
        updateById(caseInfo);
        return caseInfo;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = NAME, key = CASE_CACHE + " + #caseInfo.getId()"),
                    @CacheEvict(cacheNames = NAME, key = PROJECT_CASE_CACHE + " + #caseInfo.getProjectId()")
            }
    )
    public void delete(CaseInfo caseInfo) {
        removeById(caseInfo.getId());
    }
}
