package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.ProjectContactor;
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
import top.felixu.platform.service.ProjectContactorService;

import javax.validation.groups.Default;

/**
 * 项目和需要通知的联系人关联表
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-contactor")
public class ProjectContactorController {

    private final ProjectContactorService projectContactorService;

    @GetMapping("/{id}")
    public ProjectContactor get(@PathVariable Long id) {
        return projectContactorService.getById(id);
    }

    @GetMapping
    public IPage<ProjectContactor> page(ProjectContactor projectContactor, PageRequestForm form) {
        return projectContactorService.page(form.toPage(), new QueryWrapper<>(projectContactor));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody ProjectContactor projectContactor) {
        return projectContactorService.save(projectContactor);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody ProjectContactor projectContactor) {
        return projectContactorService.updateById(projectContactor);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return projectContactorService.removeById(id);
    }
}
