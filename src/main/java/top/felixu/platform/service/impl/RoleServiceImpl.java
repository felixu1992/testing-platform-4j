package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.RoleMapper;
import top.felixu.platform.model.entity.Role;
import top.felixu.platform.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
