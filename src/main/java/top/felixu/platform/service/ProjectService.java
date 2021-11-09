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
import top.felixu.platform.mapper.ProjectMapper;
import top.felixu.platform.model.entity.Project;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static top.felixu.platform.constants.CacheKeyConstants.Project.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.Project.PROJECT;

/**
 * 项目信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> implements IService<Project> {

    @Cacheable(cacheNames = NAME, key = PROJECT + " + #id", unless = "#result == null")
    public Project getProjectByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.PROJECT_NOT_FOUND));
    }

    public int countByGroupId(Integer groupId) {
        return count(Wrappers.<Project>lambdaQuery().eq(Project::getGroupId, groupId));
    }

    public List<Project> listByGroupId(Integer groupId) {
        return list(Wrappers.<Project>lambdaQuery().eq(Project::getGroupId, groupId));
    }

    public Map<Integer, List<Project>> mapByGroupIds(Set<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds))
            return Collections.emptyMap();
        Map<Integer, List<Project>> collect = list(Wrappers.<Project>lambdaQuery().in(Project::getGroupId, groupIds)).stream()
                .collect(Collectors.groupingBy(Project::getGroupId));
        Map<Integer, List<Project>> result = new HashMap<>(groupIds.size());
        groupIds.forEach(groupId -> result.put(groupId, Optional.ofNullable(collect.get(groupId)).orElse(Collections.emptyList())));
        return result;
    }

    public List<Project> listByProjectIds(List<Integer> projectIds) {
        if (CollectionUtils.isEmpty(projectIds))
            return Collections.emptyList();
        return list(Wrappers.<Project>lambdaQuery().in(Project::getId, projectIds));
    }

    @Cacheable(cacheNames = NAME, key = PROJECT + " + #result.getId()", unless = "#result == null")
    public Project create(Project project) {
        save(project);
        return project;
    }

    @CachePut(cacheNames = NAME, key = PROJECT + " + #result.getId()", unless = "#result == null")
    public Project update(Project project) {
        updateById(project);
        return project;
    }

    @CacheEvict(cacheNames = NAME, key = PROJECT + " + #project.getId()")
    public void delete(Project project) {
        removeById(project.getId());
    }
}
