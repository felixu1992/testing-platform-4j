package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.ContactorGroup;
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
import top.felixu.platform.service.manager.ContactorGroupManager;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 联系人分组
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "联系人分组管理")
@RequestMapping("/api/contactor-group")
public class ContactorGroupController {

    private final ContactorGroupManager contactorGroupManager;

    @GetMapping("/{id}")
    @ApiOperation("查询联系人分组详情")
    public ResponseDTO<ContactorGroup> get(@PathVariable Integer id) {
        return ResponseDTO.success(contactorGroupManager.getContactorGroupById(id));
    }

    @GetMapping
    @ApiOperation("分页查询联系人分组")
    public ResponseDTO<IPage<ContactorGroup>> page(ContactorGroup group, PageRequestForm form) {
        return ResponseDTO.success(contactorGroupManager.page(group, form));
    }

    @GetMapping("/tree")
    @ApiOperation("查询联系人分组树")
    public ResponseDTO<List<TreeNodeDTO>> tree() {
        return ResponseDTO.success(contactorGroupManager.tree());
    }

    @ApiOperation("创建联系人分组")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<ContactorGroup> create(@Validated({Create.class, Default.class}) @RequestBody ContactorGroup group) {
        return ResponseDTO.success(contactorGroupManager.create(group));
    }

    @ApiOperation("更新联系人分组")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<ContactorGroup> update(@Validated({Update.class, Default.class}) @RequestBody ContactorGroup group) {
        return ResponseDTO.success(contactorGroupManager.update(group));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除指定的联系人分组")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        contactorGroupManager.delete(id);
        return ResponseDTO.success();
    }
}
