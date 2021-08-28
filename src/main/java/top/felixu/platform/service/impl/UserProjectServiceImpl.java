package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.UserProjectMapper;
import top.felixu.platform.model.entity.UserProject;
import top.felixu.platform.service.UserProjectService;
import org.springframework.stereotype.Service;

/**
 * 用户关联项目的关联表 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProject> implements UserProjectService {

}
