package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.FileGroup;
import top.felixu.platform.service.FileGroupService;
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
 * 文件分组
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//file-group")
public class FileGroupController {

    private final FileGroupService fileGroupService;

    @GetMapping("/{id}")
    public FileGroup get(@PathVariable Long id) {
        return fileGroupService.getById(id);
    }

    @GetMapping
    public IPage<FileGroup> page(FileGroup fileGroup, PageRequestForm form) {
        return fileGroupService.page(form.toPage(), new QueryWrapper<>(fileGroup));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody FileGroup fileGroup) {
        return fileGroupService.save(fileGroup);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody FileGroup fileGroup) {
        return fileGroupService.updateById(fileGroup);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return fileGroupService.removeById(id);
    }
}
