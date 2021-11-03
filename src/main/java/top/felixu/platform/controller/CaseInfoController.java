package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.form.CaseCopyForm;
import top.felixu.platform.model.form.CaseSortForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.CaseInfo;
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
import top.felixu.platform.service.CaseInfoService;
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
@RequestMapping("/api/case-info")
public class CaseInfoController {

    private final CaseInfoManager caseInfoManager;

    @GetMapping("/{id}")
    @ApiOperation("查询用例详情")
    public ResponseDTO<CaseInfo> get(@PathVariable Integer id) {
        return ResponseDTO.success(caseInfoManager.getCaseInfoById(id));
    }

    @GetMapping
    @ApiOperation("分页查询用例")
    public ResponseDTO<IPage<CaseInfo>> page(CaseInfo caseInfo, PageRequestForm form) {
        return ResponseDTO.success(caseInfoManager.page(caseInfo, form));
    }

    @ApiOperation("创建用例")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CaseInfo> create(@Validated({Create.class, Default.class}) @RequestBody CaseInfo caseInfo) {
        return ResponseDTO.success(caseInfoManager.create(caseInfo));
    }

    @ApiOperation("复制用例")
    @PostMapping(value = "/copy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CaseInfo> copy(@Validated @RequestBody CaseCopyForm form) {
        return ResponseDTO.success(caseInfoManager.copy(form));
    }

    @ApiOperation("更新用例")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CaseInfo> update(@Validated({Update.class, Default.class}) @RequestBody CaseInfo caseInfo) {
        return ResponseDTO.success(caseInfoManager.update(caseInfo));
    }

    @ApiOperation("用例排序")
    @PutMapping(value = "/sort", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<Void> sort(@Validated @RequestBody CaseSortForm form) {
        caseInfoManager.sort(form);
        return ResponseDTO.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除用例")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        caseInfoManager.delete(id);
        return ResponseDTO.success();
    }
}
