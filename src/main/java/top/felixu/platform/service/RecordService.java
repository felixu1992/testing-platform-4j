package top.felixu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.mapper.ReportMapper;
import top.felixu.platform.model.entity.Record;
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
public class RecordService extends ServiceImpl<ReportMapper, Record> implements IService<Record> {

    @Cacheable(cacheNames = NAME, key = REPORT + " + #id", unless = "#result == null")
    public Record getReportByIdAndCheck(Integer id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new PlatformException(ErrorCode.RECORD_NOT_FOUND));
    }
}
