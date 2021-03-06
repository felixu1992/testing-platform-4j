package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.felixu.common.bean.BeanUtils;
import top.felixu.common.parameter.Joiners;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.FileInfoDTO;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.entity.FileGroup;
import top.felixu.platform.model.entity.FileInfo;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.properties.PlatformProperties;
import top.felixu.platform.service.FileGroupService;
import top.felixu.platform.service.FileInfoService;
import top.felixu.platform.util.RandomStringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoManager {

    private final FileInfoService fileInfoService;

    private final FileGroupService fileGroupService;

    private final PlatformProperties properties;

    public FileInfo getFileInfoById(Integer id) {
        return fileInfoService.getFileInfoByIdAndCheck(id);
    }

    public IPage<FileInfoDTO> page(FileInfo fileInfo, PageRequestForm form) {
        Page<FileInfo> page = fileInfoService.page(form.toPage(), Wrappers.lambdaQuery(fileInfo));
        Map<Integer, String> groupMap = fileGroupService.getFileGroupList().stream().collect(Collectors.toMap(FileGroup::getId, FileGroup::getName));
        return page.convert(item -> {
            FileInfoDTO dto = BeanUtils.map(item, FileInfoDTO.class);
            dto.setGroupName(groupMap.get(dto.getGroupId()));
            return dto;
        });
    }

    public FileInfo create(FileInfo fileInfo, MultipartFile file) {
        // ????????????????????????
        fileGroupService.getFileGroupByIdAndCheck(fileInfo.getGroupId());
        check(fileInfo);
        try {
            fileInfo.setPath(upload(file));
        } catch (IOException ex) {
            log.error(ErrorCode.FILE_SAVE_FAILED.getMessage(), ex);
            throw new PlatformException(ErrorCode.FILE_SAVE_FAILED);
        }
        return fileInfoService.create(fileInfo);
    }

    public FileInfo update(FileInfo fileInfo, MultipartFile file) {
        FileInfo original = fileInfoService.getFileInfoByIdAndCheck(fileInfo.getId());
        BeanUtils.copyNotEmpty(FileInfo.class, fileInfo, FileInfo.class, original);
        // ????????????????????????
        fileGroupService.getFileGroupByIdAndCheck(original.getGroupId());
        check(original);
        if (file != null) {
            try {
                // ???????????????
                Files.deleteIfExists(Paths.get(original.getPath()));
                original.setPath(upload(file));
            } catch (IOException ex) {
                log.error(ErrorCode.FILE_UPDATE_FAILED.getMessage(), ex);
                throw new PlatformException(ErrorCode.FILE_UPDATE_FAILED);
            }
        }
        return fileInfoService.update(original);
    }

    public void delete(Integer id) {
        FileInfo fileInfo = fileInfoService.getFileInfoByIdAndCheck(id);
        // TODO: 09/03 ?????????????????????
        try {
            // ???????????????
            Files.deleteIfExists(Paths.get(fileInfo.getPath()));
            fileInfoService.delete(fileInfo);
        } catch (IOException ex) {
            log.error(ErrorCode.FILE_DELETE_FAILED.getMessage(), ex);
            throw new PlatformException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private String upload(MultipartFile file) throws IOException {
        Path path = Paths.get(properties.getFileStorage());
        if (Files.notExists(path))
            Files.createDirectory(path);
        Path temp = path.resolve(Joiners.UNDERSCORE.join(RandomStringUtils.make(), file.getOriginalFilename()));
        Path target = Files.createFile(temp);
        Files.write(target, file.getBytes());
        return target.toAbsolutePath().toString();
    }

    private void check(FileInfo fileInfo) {
        // ????????????
        FileInfo name = fileInfoService.getOne(Wrappers.<FileInfo>lambdaQuery().eq(FileInfo::getName, fileInfo.getName()));
        if (name != null && (fileInfo.getId() == null || !fileInfo.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.FILE_DUPLICATE_NAME);
    }
}