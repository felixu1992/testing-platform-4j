package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.ReportMapper;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.entity.Report;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static top.felixu.platform.constants.CacheKeyConstants.Report.NAME;
import static top.felixu.platform.constants.CacheKeyConstants.Report.REPORT;

/**
 * 用例测试记录 服务实现类
 *
 * @author felixu
 * @since 2021-08-28
 */
@Service
public class ReportService extends ServiceImpl<ReportMapper, Report> implements IService<Report> {

    @Cacheable(cacheNames = NAME, key = REPORT + " + #id", unless = "#result == null")
    public Report getReportByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.RECORD_NOT_FOUND));
    }
}
