package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;
import top.felixu.platform.model.dto.ProjectDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.dto.StatisticsDTO;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.form.CaseExecuteForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.form.ProjectCopyForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.ProjectExecute;
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
import top.felixu.platform.service.manager.CaseInfoManager;
import top.felixu.platform.service.manager.ProjectManager;

import javax.servlet.http.HttpServletResponse;
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

    private final CaseInfoManager caseInfoManager;

    @GetMapping("/{id}")
    @ApiOperation("查询项目详情")
    public RespDTO<Project> get(@PathVariable Integer id) {
        return RespDTO.success(projectManager.getProjectById(id));
    }

    @GetMapping
    @ApiOperation("分页查询项目列表")
    public RespDTO<IPage<ProjectDTO>> page(Project project, PageRequestForm form) {
        return RespDTO.success(projectManager.page(project, form));
    }

    @ApiOperation("创建项目")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<Project> create(@Validated({Create.class, Default.class}) @RequestBody Project project) {
        return RespDTO.success(projectManager.create(project));
    }

    @ApiOperation("创建项目副本")
    @PostMapping("/copy")
    public RespDTO<Project> copy(@Validated @RequestBody ProjectCopyForm form) {
        return RespDTO.success(projectManager.copy(form));
    }

    @ApiOperation("用例执行")
    @PostMapping("/execute")
    public RespDTO<Record> execute(@Validated({ProjectExecute.class, Default.class}) @RequestBody CaseExecuteForm form) {
        return RespDTO.success(caseInfoManager.execute(form));
    }
    // TODO: 09/06 导入

    @ApiOperation("项目导入")
    @PostMapping("/import")
    public RespDTO<Void> importProject() {
        return RespDTO.success();
    }


    // TODO: 09/06 导出
    @ApiOperation("项目导出")
    @GetMapping("/export/{id}")
    public void export(@ApiParam("项目ID") @PathVariable("id") String id, @ApiIgnore HttpServletResponse response) {
        projectManager.export(id, response);
    }

    @ApiOperation("更新项目")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<Project> update(@Validated({Update.class, Default.class}) @RequestBody Project project) {
        return RespDTO.success(projectManager.update(project));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除项目")
    public RespDTO<Void> delete(@PathVariable Integer id) {
        projectManager.delete(id);
        return RespDTO.success();
    }

    @GetMapping("/statistics")
    @ApiOperation("首页项目统计信息")
    public RespDTO<StatisticsDTO> statistics() {
        return RespDTO.success(projectManager.statistics());
    }
}
