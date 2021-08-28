package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.felixu.platform.mapper.ReportMapper;
import top.felixu.platform.model.entity.Report;
import org.springframework.stereotype.Service;

/**
 * 用例测试记录 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ReportService extends ServiceImpl<ReportMapper, Report> implements IService<Report> {

}
