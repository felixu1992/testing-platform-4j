package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.model.dto.ReportDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.manager.ReportManager;

/**
 * 测试报告
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

    @GetMapping
    @ApiOperation("分页查询用例执行报告")
    public RespDTO<IPage<ReportDTO>> page(Report report, PageRequestForm form) {
        return RespDTO.success(reportManager.page(report, form));
    }

//    @DeleteMapping("/{id}")
//    public boolean delete(@PathVariable Long id) {
//        return recordService.removeById(id);
//    }
}
