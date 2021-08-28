package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.service.CaseInfoService;
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
import javax.validation.groups.Default;

/**
 * 用例信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//case-info")
public class CaseInfoController {

    private final CaseInfoService caseInfoService;

    @GetMapping("/{id}")
    public CaseInfo get(@PathVariable Long id) {
        return caseInfoService.getById(id);
    }

    @GetMapping
    public IPage<CaseInfo> page(CaseInfo caseInfo, PageRequestForm form) {
        return caseInfoService.page(form.toPage(), new QueryWrapper<>(caseInfo));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody CaseInfo caseInfo) {
        return caseInfoService.save(caseInfo);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody CaseInfo caseInfo) {
        return caseInfoService.updateById(caseInfo);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return caseInfoService.removeById(id);
    }
}
