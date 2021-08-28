package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.FileInfo;
import top.felixu.platform.service.FileInfoService;
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
 * 文件信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//file-info")
public class FileInfoController {

    private final FileInfoService fileInfoService;

    @GetMapping("/{id}")
    public FileInfo get(@PathVariable Long id) {
        return fileInfoService.getById(id);
    }

    @GetMapping
    public IPage<FileInfo> page(FileInfo fileInfo, PageRequestForm form) {
        return fileInfoService.page(form.toPage(), new QueryWrapper<>(fileInfo));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody FileInfo fileInfo) {
        return fileInfoService.save(fileInfo);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody FileInfo fileInfo) {
        return fileInfoService.updateById(fileInfo);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return fileInfoService.removeById(id);
    }
}
