package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.CaseInfoGroup;
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
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.manager.CaseInfoGroupManager;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 用例分类
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "用例分组管理")
@RequestMapping("/api/case-info-group")
public class CaseInfoGroupController {

    private final CaseInfoGroupManager caseInfoGroupManager;

    @GetMapping("/{id}")
    @ApiOperation("查询用例分组详情")
    public ResponseDTO<CaseInfoGroup> get(@PathVariable Integer id) {
        return ResponseDTO.success(caseInfoGroupManager.getCaseInfoGroupById(id));
    }

    @GetMapping
    @ApiOperation("分页查询用例分组")
    public ResponseDTO<IPage<CaseInfoGroup>> page(CaseInfoGroup group, PageRequestForm form) {
        return ResponseDTO.success(caseInfoGroupManager.page(group, form));
    }

    @GetMapping("/tree")
    @ApiOperation("查询用例分组树")
    public ResponseDTO<List<TreeNodeDTO>> tree(@RequestParam Integer projectId) {
        return ResponseDTO.success(caseInfoGroupManager.tree(projectId));
    }

    @ApiOperation("创建用例分组")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CaseInfoGroup> create(@Validated({Create.class, Default.class}) @RequestBody CaseInfoGroup group) {
        return ResponseDTO.success(caseInfoGroupManager.create(group));
    }

    @ApiOperation("更新用例分组")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CaseInfoGroup> update(@Validated({Update.class, Default.class}) @RequestBody CaseInfoGroup group) {
        return ResponseDTO.success(caseInfoGroupManager.update(group));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除指定的用例分组")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        caseInfoGroupManager.delete(id);
        return ResponseDTO.success();
    }
}
