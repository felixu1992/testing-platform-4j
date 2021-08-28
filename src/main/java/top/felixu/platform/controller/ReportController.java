package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.Report;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.felixu.platform.service.ReportService;

import javax.validation.groups.Default;

/**
 * 用例测试记录
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{id}")
    public Report get(@PathVariable Long id) {
        return reportService.getById(id);
    }

    @GetMapping
    public IPage<Report> page(Report report, PageRequestForm form) {
        return reportService.page(form.toPage(), new QueryWrapper<>(report));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody Report report) {
        return reportService.save(report);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody Report report) {
        return reportService.updateById(report);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return reportService.removeById(id);
    }
}
