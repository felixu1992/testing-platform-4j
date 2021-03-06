package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.model.dto.CaseInfoDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.form.CaseCopyForm;
import top.felixu.platform.model.form.CaseExecuteForm;
import top.felixu.platform.model.form.CaseSortForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.CaseExecute;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.service.manager.CaseInfoManager;

import javax.validation.groups.Default;

/**
 * 用例信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@Api(tags = "用例管理")
@RequiredArgsConstructor
@RequestMapping("/api/case")
public class CaseInfoController {

    private final CaseInfoManager caseInfoManager;

    @GetMapping("/{id}")
    @ApiOperation("查询用例详情")
    public RespDTO<CaseInfoDTO> get(@PathVariable Integer id) {
        return RespDTO.success(caseInfoManager.getCaseInfoById(id));
    }

    @GetMapping
    @ApiOperation("分页查询用例")
    public RespDTO<IPage<CaseInfoDTO>> page(CaseInfo caseInfo, PageRequestForm form) {
        return RespDTO.success(caseInfoManager.page(caseInfo, form));
    }

    @ApiOperation("创建用例")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<CaseInfo> create(@Validated({Create.class, Default.class}) @RequestBody CaseInfo caseInfo) {
        return RespDTO.success(caseInfoManager.create(caseInfo));
    }

    @ApiOperation("复制用例")
    @PostMapping(value = "/copy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<CaseInfo> copy(@Validated @RequestBody CaseCopyForm form) {
        return RespDTO.success(caseInfoManager.copy(form));
    }

    @ApiOperation("更新用例")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<CaseInfo> update(@Validated({Update.class, Default.class}) @RequestBody CaseInfo caseInfo) {
        return RespDTO.success(caseInfoManager.update(caseInfo));
    }

    @ApiOperation("用例排序")
    @PutMapping(value = "/sort", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<Void> sort(@Validated @RequestBody CaseSortForm form) {
        caseInfoManager.sort(form);
        return RespDTO.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除用例")
    public RespDTO<Void> delete(@PathVariable Integer id) {
        caseInfoManager.delete(id);
        return RespDTO.success();
    }

    @PostMapping("/execute")
    @ApiOperation("执行用例")
    public RespDTO<Report> execute(@Validated({CaseExecute.class, Default.class}) @RequestBody CaseExecuteForm form) {
        return RespDTO.success(caseInfoManager.execute(form));
    }
}
