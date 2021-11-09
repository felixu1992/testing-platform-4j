package top.felixu.platform.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.model.form.UserProjectForm;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.UserProjectService;
import top.felixu.platform.service.UserService;
import top.felixu.platform.util.UserHolderUtils;

import java.util.List;

/**
 * @author felixu
 * @since 2021.11.09
 */
@Service
@RequiredArgsConstructor
public class UserProjectManager {

    private final UserService userService;

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    public void updateRelation(UserProjectForm form) {
        // 是否有操作权限，仅自己的管理员可操作
        User target = userService.getUserByIdAndCheck(form.getUserId());
        if (UserHolderUtils.getCurrentRole() != RoleTypeEnum.ADMIN || !UserHolderUtils.getCurrentUserId().equals(target.getParentId()))
            throw new PlatformException(ErrorCode.MISSING_AUTHORITY);
        // 判断项目是否都存在
        if (!CollectionUtils.isEmpty(form.getProjectIds())) {
            List<Project> projects = projectService.listByProjectIds(form.getProjectIds());
            if (projects.size() != form.getProjectIds().size())
                throw new PlatformException(ErrorCode.PARAM_ERROR);
        }
        // 更新关联关系
        userProjectService.updateRelation(form.getUserId(), form.getProjectIds());
    }
}
