package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.RecordMapper;
import top.felixu.platform.model.entity.Report;
import java.util.Optional;
import static top.felixu.platform.constants.CacheKeyConstants.Report.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.Report.REPORT;

/**
 * 测试报告 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ReportService extends ServiceImpl<RecordMapper, Report> implements IService<Report> {

    @Cacheable(cacheNames = NAME, key = REPORT + " + #id", unless = "#result == null")
    public Report getReportByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.REPORT_NOT_FOUND));
    }
}
