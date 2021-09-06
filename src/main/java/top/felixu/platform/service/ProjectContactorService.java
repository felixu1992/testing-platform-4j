package top.felixu.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;
import top.felixu.platform.mapper.ProjectContactorMapper;
import top.felixu.platform.model.entity.ProjectContactor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 项目和需要通知的联系人关联表 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ProjectContactorService extends ServiceImpl<ProjectContactorMapper, ProjectContactor> implements IService<ProjectContactor> {

    public List<Integer> getContactorsByProjectId(Integer projectId) {
        return list(Wrappers.<ProjectContactor>lambdaQuery().eq(ProjectContactor::getProjectId, projectId))
                .parallelStream()
                .map(ProjectContactor::getContactorId)
                .collect(Collectors.toList());
    }

    public List<Integer> getProjectIdsByContactorId(Integer contactorId) {
        return list(Wrappers.<ProjectContactor>lambdaQuery().eq(ProjectContactor::getContactorId, contactorId))
                .parallelStream()
                .map(ProjectContactor::getProjectId)
                .collect(Collectors.toList());
    }

    public void update(List<ProjectContactor> relations) {
        if (CollectionUtils.isEmpty(relations))
            return;
        // 旧的关系
        Map<Integer, Integer> relationMap = list(Wrappers.<ProjectContactor>lambdaQuery().eq(ProjectContactor::getProjectId, relations.get(0).getProjectId()))
                .parallelStream().collect(Collectors.toMap(ProjectContactor::getContactorId, ProjectContactor::getId));
        // 旧的联系人列表
        Set<Integer> contactors = relationMap.keySet();
        // 过滤出需要存储的关系
        relations = relations.parallelStream().filter(relation -> {
            if (contactors.contains(relation.getContactorId())) {
                // 旧的中移除当前列表中包含的，剩余的就是需要删除的联系人
                relationMap.remove(relation.getContactorId());
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        // 新增新的关联关系
        saveBatch(relations);
        // 删掉多余的关联关系
        removeByIds(relationMap.values());
    }
}
