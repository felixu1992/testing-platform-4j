package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.FileGroupMapper;
import top.felixu.platform.model.entity.FileGroup;
import org.springframework.stereotype.Service;

/**
 * 文件分组 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class FileGroupService extends ServiceImpl<FileGroupMapper, FileGroup> implements IService<FileGroup> {

}
