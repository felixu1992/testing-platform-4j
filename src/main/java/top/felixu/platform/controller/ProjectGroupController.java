package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.ProjectGroup;
import top.felixu.platform.service.ProjectGroupService;
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
 * 项目分组
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//project-group")
public class ProjectGroupController {

    private final ProjectGroupService projectGroupService;

    @GetMapping("/{id}")
    public ProjectGroup get(@PathVariable Long id) {
        return projectGroupService.getById(id);
    }

    @GetMapping
    public IPage<ProjectGroup> page(ProjectGroup projectGroup, PageRequestForm form) {
        return projectGroupService.page(form.toPage(), new QueryWrapper<>(projectGroup));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody ProjectGroup projectGroup) {
        return projectGroupService.save(projectGroup);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody ProjectGroup projectGroup) {
        return projectGroupService.updateById(projectGroup);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return projectGroupService.removeById(id);
    }
}
