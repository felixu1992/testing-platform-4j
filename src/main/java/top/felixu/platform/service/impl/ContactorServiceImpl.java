package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ContactorMapper;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.service.ContactorService;
import org.springframework.stereotype.Service;

/**
 * 联系人 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ContactorServiceImpl extends ServiceImpl<ContactorMapper, Contactor> implements ContactorService {

}
