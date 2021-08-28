package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.FileInfoMapper;
import top.felixu.platform.model.entity.FileInfo;
import top.felixu.platform.service.FileInfoService;
import org.springframework.stereotype.Service;

/**
 * 文件信息 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

}
