package top.felixu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.RecordMapper;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.service.RecordService;
import org.springframework.stereotype.Service;

/**
 * 测试报告 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

}
