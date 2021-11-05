package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import top.felixu.platform.model.dto.FileInfoDTO;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.FileInfo;
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
import top.felixu.platform.service.FileInfoService;
import top.felixu.platform.service.manager.FileInfoManager;

import javax.validation.groups.Default;

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
    public ResponseDTO<FileInfo> get(@PathVariable Integer id) {
        return ResponseDTO.success(fileInfoManager.getFileInfoById(id));
    }

    @GetMapping
    @ApiOperation("分页查询文件")
    public ResponseDTO<IPage<FileInfoDTO>> page(FileInfo fileInfo, PageRequestForm form) {
        return ResponseDTO.success(fileInfoManager.page(fileInfo, form));
    }

    @ApiOperation("上传文件")
    @PostMapping
    public ResponseDTO<FileInfo> create(@RequestParam String name, @RequestParam Integer groupId,
                                        @RequestParam(required = false) String remark, @RequestPart MultipartFile files) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(name);
        fileInfo.setGroupId(groupId);
        fileInfo.setRemark(remark);
        return ResponseDTO.success(fileInfoManager.create(fileInfo, files));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<FileInfo> update(@RequestParam Integer id, @RequestParam String name, @RequestParam Integer groupId,
                                        @RequestParam(required = false) String remark, @RequestPart(required = false) MultipartFile files) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(id);
        fileInfo.setName(name);
        fileInfo.setGroupId(groupId);
        fileInfo.setRemark(remark);
        return ResponseDTO.success(fileInfoManager.update(fileInfo, files));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除文件")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        fileInfoManager.delete(id);
        return ResponseDTO.success();
    }
}
