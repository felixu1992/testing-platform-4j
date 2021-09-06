package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.ProjectGroupMapper;
import top.felixu.platform.model.entity.ContactorGroup;
import top.felixu.platform.model.entity.ProjectGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static top.felixu.platform.constants.CacheKeyConstants.ProjectGroup.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.ProjectGroup.PROJECT_GROUP_CACHE;
import static top.felixu.platform.constants.CacheKeyConstants.ProjectGroup.PROJECT_GROUP_LIST_CACHE;

/**
 * 项目分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectGroupService extends ServiceImpl<ProjectGroupMapper, ProjectGroup> implements IService<ProjectGroup> {

    @Cacheable(cacheNames = NAME, key = PROJECT_GROUP_CACHE + " + #id", unless = "#result == null", sync = true)
    public ProjectGroup getProjectGroupByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.CONTACTOR_GROUP_NOT_FOUND));
    }

    @Cacheable(cacheNames = NAME, key = PROJECT_GROUP_LIST_CACHE + " + T(top.felixu.platform.util.UserHolderUtils).getOwner()")
    public List<ProjectGroup> getProjectGroupList() {
        return list();
    }

    @Caching(
            cacheable = @Cacheable(cacheNames = NAME, key = PROJECT_GROUP_CACHE + " + #result.getId()", unless = "#result == null", sync = true),
            evict = @CacheEvict(cacheNames = NAME, key = PROJECT_GROUP_LIST_CACHE + " + #result.getOwner()")
    )
    public ProjectGroup create(ProjectGroup group) {
        save(group);
        return group;
    }

    @Caching(
            put = @CachePut(cacheNames = NAME, key = PROJECT_GROUP_CACHE + " + #result.getId()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = NAME, key = PROJECT_GROUP_LIST_CACHE + " + #result.getOwner()")
    )
    public ProjectGroup update(ProjectGroup group) {
        updateById(group);
        return group;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = NAME, key = PROJECT_GROUP_LIST_CACHE + " + #group.getOwner()"),
                    @CacheEvict(cacheNames = NAME, key = PROJECT_GROUP_CACHE + " + #group.getId()")
            }
    )
    public void delete(ProjectGroup group) {
        removeById(group.getId());
    }
}