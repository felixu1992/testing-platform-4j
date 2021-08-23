package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.UserMapper;
import top.felixu.platform.model.entity.User;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author felixu
 * @since 2021-08-23
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {

}
