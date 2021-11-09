package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import top.felixu.platform.model.dto.FileInfoDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.entity.FileInfo;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.service.manager.FileInfoManager;

/**
 * 文件信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@Api(tags = "文件管理")
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileInfoController {

    private final FileInfoManager fileInfoManager;

    @GetMapping("/{id}")
    @ApiOperation("查询文件详情")
    public RespDTO<FileInfo> get(@PathVariable Integer id) {
        return RespDTO.success(fileInfoManager.getFileInfoById(id));
    }

    @GetMapping
    @ApiOperation("分页查询文件")
    public RespDTO<IPage<FileInfoDTO>> page(FileInfo fileInfo, PageRequestForm form) {
        return RespDTO.success(fileInfoManager.page(fileInfo, form));
    }

    @ApiOperation("上传文件")
    @PostMapping
    public RespDTO<FileInfo> create(@RequestParam String name, @RequestParam Integer groupId,
                                    @RequestParam(required = false) String remark, @RequestPart MultipartFile files) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(name);
        fileInfo.setGroupId(groupId);
        fileInfo.setRemark(remark);
        return RespDTO.success(fileInfoManager.create(fileInfo, files));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RespDTO<FileInfo> update(@RequestParam Integer id, @RequestParam String name, @RequestParam Integer groupId,
                                    @RequestParam(required = false) String remark, @RequestPart(required = false) MultipartFile files) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(id);
        fileInfo.setName(name);
        fileInfo.setGroupId(groupId);
        fileInfo.setRemark(remark);
        return RespDTO.success(fileInfoManager.update(fileInfo, files));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除文件")
    public RespDTO<Void> delete(@PathVariable Integer id) {
        fileInfoManager.delete(id);
        return RespDTO.success();
    }
}
