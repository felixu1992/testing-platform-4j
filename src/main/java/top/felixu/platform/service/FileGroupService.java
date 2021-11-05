package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.FileGroupMapper;
import top.felixu.platform.model.entity.FileGroup;

import java.util.List;
import java.util.Optional;

import static top.felixu.platform.constants.CacheKeyConstants.FileGroup.FILE_GROUP;
import static top.felixu.platform.constants.CacheKeyConstants.FileGroup.FILE_GROUP_LIST;
import static top.felixu.platform.constants.CacheKeyConstants.FileGroup.NAME;

/**
 * 文件分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class FileGroupService extends ServiceImpl<FileGroupMapper, FileGroup> implements IService<FileGroup> {

    @Cacheable(cacheNames = NAME, key = FILE_GROUP + " + #id", unless = "#result == null")
    public FileGroup getFileGroupByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.FILE_GROUP_NOT_FOUND));
    }

    @Cacheable(cacheNames = NAME, key = FILE_GROUP_LIST + " + T(top.felixu.platform.util.UserHolderUtils).getOwner()")
    public List<FileGroup> getFileGroupList() {
        return list();
    }

    @Caching(
            cacheable = @Cacheable(cacheNames = NAME, key = FILE_GROUP + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = FILE_GROUP_LIST + " + #result.getOwner()")
    )
    public FileGroup create(FileGroup group) {
        save(group);
        return group;
    }

    @Caching(
            put = @CachePut(cacheNames = NAME, key = FILE_GROUP + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = FILE_GROUP_LIST + " + #result.getOwner()")
    )
    public FileGroup update(FileGroup group) {
        updateById(group);
        return group;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = NAME, key = FILE_GROUP_LIST + " + #group.getOwner()"),
                    @CacheEvict(cacheNames = NAME, key = FILE_GROUP + " + #group.getId()")
            }
    )
    public void delete(FileGroup group) {
        removeById(group.getId());
    }
}
