package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import top.felixu.platform.model.dto.ReportDTO;
import top.felixu.platform.model.dto.RespDTO;
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
import top.felixu.platform.service.manager.ReportManager;

import javax.validation.groups.Default;

/**
 * 用例测试记录
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "用例执行报告")
@RequestMapping("/api/report")
public class ReportController {

    private final ReportManager reportManager;
//
//    @GetMapping("/{id}")
//    public Report get(@PathVariable Long id) {
//        return reportService.getById(id);
//    }

    @GetMapping
    public RespDTO<IPage<ReportDTO>> page(Report report, PageRequestForm form) {
        return RespDTO.success(reportManager.page(report, form));
    }
//
//    @DeleteMapping("/{id}")
//    public boolean delete(@PathVariable Long id) {
//        return reportService.removeById(id);
//    }
}
