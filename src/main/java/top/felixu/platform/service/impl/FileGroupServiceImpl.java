package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.FileGroupMapper;
import top.felixu.platform.model.entity.FileGroup;
import top.felixu.platform.service.FileGroupService;
import org.springframework.stereotype.Service;

/**
 * 文件分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class FileGroupServiceImpl extends ServiceImpl<FileGroupMapper, FileGroup> implements FileGroupService {

}
