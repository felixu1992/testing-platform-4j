package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.Project;
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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.manager.ProjectManager;

import javax.validation.groups.Default;

/**
 * 项目信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@Api(tags = "项目管理")
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectManager projectManager;

    @GetMapping("/{id}")
    @ApiOperation("查询项目详情")
    public ResponseDTO<Project> get(@PathVariable Integer id) {
        return ResponseDTO.success(projectManager.getProjectById(id));
    }

    @GetMapping
    @ApiOperation("分页查询项目列表")
    public ResponseDTO<IPage<Project>> page(Project project, PageRequestForm form) {
        return ResponseDTO.success(projectManager.page(project, form));
    }

    @ApiOperation("创建项目")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<Project> create(@Validated({Create.class, Default.class}) @RequestBody Project project) {
        return ResponseDTO.success(projectManager.create(project));
    }

    // TODO: 09/06 复制 复制项目和用例
    // TODO: 09/06 执行用例
    // TODO: 09/06 导入
    // TODO: 09/06 导出

    @ApiOperation("更新项目")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<Project> update(@Validated({Update.class, Default.class}) @RequestBody Project project) {
        return ResponseDTO.success(projectManager.update(project));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除项目")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        projectManager.delete(id);
        return ResponseDTO.success();
    }
}
