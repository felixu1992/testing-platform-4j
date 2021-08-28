package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.service.ProjectService;
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
import javax.validation.groups.Default;

/**
 * 项目信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public Project get(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @GetMapping
    public IPage<Project> page(Project project, PageRequestForm form) {
        return projectService.page(form.toPage(), new QueryWrapper<>(project));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody Project project) {
        return projectService.save(project);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody Project project) {
        return projectService.updateById(project);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return projectService.removeById(id);
    }
}
