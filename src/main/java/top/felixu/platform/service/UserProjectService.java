package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.UserProjectMapper;
import top.felixu.platform.model.entity.UserProject;
import org.springframework.stereotype.Service;
import top.felixu.platform.util.UserHolderUtils;

import java.util.List;
import java.util.stream.Collectors;

import static top.felixu.platform.constants.CacheKeyConstants.UserProject.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.UserProject.RELATION;

/**
 * 用户关联项目的关联表 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class UserProjectService extends ServiceImpl<UserProjectMapper, UserProject> implements IService<UserProject> {

    @Cacheable(cacheNames = NAME, key = RELATION + " + #userId", unless = "#result == null")
    public List<Integer> getProjectIdsByUserId(Integer userId) {
        return list(Wrappers.<UserProject>lambdaQuery().eq(UserProject::getUserId, userId))
                .stream()
                .map(UserProject::getProjectId)
                .collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = NAME, key = RELATION + " + #userId")
    public void updateRelation(Integer userId, List<Integer> projectIds) {
        // 移除之前所有的关联关系
        remove(Wrappers.<UserProject>lambdaQuery().eq(UserProject::getUserId, userId));
        // 重新构建关联关系
        List<UserProject> relations = projectIds.stream().map(projectId -> {
            UserProject relation = new UserProject();
            relation.setUserId(userId);
            relation.setProjectId(projectId);
            return relation;
        }).collect(Collectors.toList());
        saveBatch(relations);
    }

    @CacheEvict(cacheNames = NAME, key = RELATION + " + #userId")
    public void createRelation(Integer userId, Integer projectId) {
        UserProject relation = new UserProject();
        relation.setUserId(userId);
        relation.setProjectId(projectId);
        save(relation);
    }

    @CacheEvict(cacheNames = NAME)
    public void deleteRelationByProjectId(Integer projectId) {
        remove(Wrappers.<UserProject>lambdaQuery().eq(UserProject::getProjectId, projectId));
    }

    @CacheEvict(cacheNames = NAME, key = RELATION + " + #userId")
    public void deleteRelationByUserId(Integer userId) {
        remove(Wrappers.<UserProject>lambdaQuery().eq(UserProject::getUserId, userId));
    }

    public void checkAuthority(Integer projectId) {
        if (UserHolderUtils.getCurrentRole() == RoleTypeEnum.ORDINARY) {
            if (count(Wrappers.<UserProject>lambdaQuery()
                    .eq(UserProject::getUserId, UserHolderUtils.getCurrentUserId())
                    .eq(UserProject::getProjectId, projectId)) <= 0)
                throw new PlatformException(ErrorCode.MISSING_AUTHORITY);
        }
    }
}