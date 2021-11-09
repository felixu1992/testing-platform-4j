package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.FileGroup;
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
import top.felixu.platform.service.manager.FileGroupManager;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 文件分组
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "文件分组管理")
@RequestMapping("/api/file-group")
public class FileGroupController {

    private final FileGroupManager fileGroupManager;

    @GetMapping("/{id}")
    @ApiOperation("查询文件分组详情")
    public RespDTO<FileGroup> get(@PathVariable Integer id) {
        return RespDTO.success(fileGroupManager.getFileGroupById(id));
    }

    @GetMapping
    @ApiOperation("分页查询文件分组")
    public RespDTO<IPage<FileGroup>> page(FileGroup group, PageRequestForm form) {
        return RespDTO.success(fileGroupManager.page(group, form));
    }

    @GetMapping("/tree")
    @ApiOperation("查询文件分组树")
    public RespDTO<List<TreeNodeDTO>> tree() {
        return RespDTO.success(fileGroupManager.tree());
    }

    @ApiOperation("创建文件分组")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<FileGroup> create(@Validated({Create.class, Default.class}) @RequestBody FileGroup group) {
        return RespDTO.success(fileGroupManager.create(group));
    }

    @ApiOperation("更新文件分组")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<FileGroup> update(@Validated({Update.class, Default.class}) @RequestBody FileGroup group) {
        return RespDTO.success(fileGroupManager.update(group));
    }

    @ApiOperation("删除指定的文件分组")
    @DeleteMapping("/{id}")
    public RespDTO<Void> delete(@PathVariable Integer id) {
        fileGroupManager.delete(id);
        return RespDTO.success();
    }
}
