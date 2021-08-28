package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.UserProject;
import top.felixu.platform.service.UserProjectService;
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
 * 用户关联项目的关联表
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//user-project")
public class UserProjectController {

    private final UserProjectService userProjectService;

    @GetMapping("/{id}")
    public UserProject get(@PathVariable Long id) {
        return userProjectService.getById(id);
    }

    @GetMapping
    public IPage<UserProject> page(UserProject userProject, PageRequestForm form) {
        return userProjectService.page(form.toPage(), new QueryWrapper<>(userProject));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody UserProject userProject) {
        return userProjectService.save(userProject);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody UserProject userProject) {
        return userProjectService.updateById(userProject);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return userProjectService.removeById(id);
    }
}
