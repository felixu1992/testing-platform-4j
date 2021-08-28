package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ContactorMapper;
import top.felixu.platform.model.entity.Contactor;
import org.springframework.stereotype.Service;

/**
 * 联系人 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ContactorService extends ServiceImpl<ContactorMapper, Contactor> implements IService<Contactor> {

}
