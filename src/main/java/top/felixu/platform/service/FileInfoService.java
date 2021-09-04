package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.FileInfoMapper;
import top.felixu.platform.model.entity.FileInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static top.felixu.platform.constants.CacheKeyConstants.File.FILE_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.File.NAME;

/**
 * 文件信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class FileInfoService extends ServiceImpl<FileInfoMapper, FileInfo> implements IService<FileInfo> {

    @Cacheable(cacheNames = NAME, key = FILE_CACHE + " + #id", unless = "#result == null", sync = true)
    public FileInfo getFileInfoByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.FILE_NOT_FOUND));
    }

    public int countByGroupId(Integer groupId) {
        return count(Wrappers.<FileInfo>lambdaQuery().eq(FileInfo::getGroupId, groupId));
    }

    public List<FileInfo> listByGroupId(Integer groupId) {
        return list(Wrappers.<FileInfo>lambdaQuery().eq(FileInfo::getGroupId, groupId));
    }

    public Map<Integer, List<FileInfo>> mapByGroupIds(Set<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds))
            return Collections.emptyMap();
        Map<Integer, List<FileInfo>> collect = list(Wrappers.<FileInfo>lambdaQuery().in(FileInfo::getGroupId, groupIds)).stream()
                .collect(Collectors.groupingBy(FileInfo::getGroupId));
        Map<Integer, List<FileInfo>> result = new HashMap<>(groupIds.size());
        groupIds.forEach(groupId -> result.put(groupId, Optional.ofNullable(collect.get(groupId)).orElse(Collections.emptyList())));
        return result;
    }

    @Cacheable(cacheNames = NAME, key = FILE_CACHE + " + #result.getId()", unless = "#result == null", sync = true)
    public FileInfo create(FileInfo fileInfo) {
        save(fileInfo);
        return fileInfo;
    }

    @CachePut(cacheNames = NAME, key = FILE_CACHE + " + #result.getId()", unless = "#result == null")
    public FileInfo update(FileInfo fileInfo) {
        updateById(fileInfo);
        return fileInfo;
    }

    @CacheEvict(cacheNames = NAME, key = FILE_CACHE + " + #fileInfo.getId()")
    public void delete(FileInfo fileInfo) {
        removeById(fileInfo.getId());
    }
}
