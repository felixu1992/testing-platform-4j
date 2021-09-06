package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.ProjectGroup;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.service.manager.ProjectGroupManager;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 项目分组
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "项目分组管理")
@RequestMapping("/api/project-group")
public class ProjectGroupController {

    private final ProjectGroupManager projectGroupManager;

    @GetMapping("/{id}")
    @ApiOperation("查询项目分组详情")
    public ResponseDTO<ProjectGroup> get(@PathVariable Integer id) {
        return ResponseDTO.success(projectGroupManager.getProjectGroupById(id));
    }

    @GetMapping
    @ApiOperation("分页查询项目分组")
    public ResponseDTO<IPage<ProjectGroup>> page(ProjectGroup group, PageRequestForm form) {
        return ResponseDTO.success(projectGroupManager.page(group, form));
    }

    @GetMapping("/tree")
    @ApiOperation("查询项目分组树")
    public ResponseDTO<List<TreeNodeDTO>> tree() {
        return ResponseDTO.success(projectGroupManager.tree());
    }

    @ApiOperation("创建项目分组")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<ProjectGroup> create(@Validated({Create.class, Default.class}) @RequestBody ProjectGroup group) {
        return ResponseDTO.success(projectGroupManager.create(group));
    }

    @ApiOperation("更新项目分组")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<ProjectGroup> update(@Validated({Update.class, Default.class}) @RequestBody ProjectGroup group) {
        return ResponseDTO.success(projectGroupManager.update(group));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除指定的项目分组")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        projectGroupManager.delete(id);
        return ResponseDTO.success();
    }
}
