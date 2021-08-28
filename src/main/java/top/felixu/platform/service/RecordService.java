package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.RecordMapper;
import top.felixu.platform.model.entity.Record;
import org.springframework.stereotype.Service;

/**
 * 测试报告 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class RecordService extends ServiceImpl<RecordMapper, Record> implements IService<Record> {

}
