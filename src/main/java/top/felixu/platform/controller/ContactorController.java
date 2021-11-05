package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.ContactorDTO;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.Contactor;
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
import top.felixu.platform.service.manager.ContactorManager;
import javax.validation.groups.Default;

/**
 * 联系人
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@Api(tags = "联系人管理")
@RequiredArgsConstructor
@RequestMapping("/api/contactor")
public class ContactorController {

    private final ContactorManager contactorManager;

    @GetMapping("/{id}")
    @ApiOperation("查询联系人详情")
    public ResponseDTO<Contactor> get(@PathVariable Integer id) {
        return ResponseDTO.success(contactorManager.getContactorById(id));
    }

    @GetMapping
    @ApiOperation("分页查询联系人")
    public ResponseDTO<IPage<ContactorDTO>> page(Contactor contactor, PageRequestForm form) {
        return ResponseDTO.success(contactorManager.page(contactor, form));
    }

    @ApiOperation("创建联系人")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<Contactor> create(@Validated({Create.class, Default.class}) @RequestBody Contactor contactor) {
        return ResponseDTO.success(contactorManager.create(contactor));
    }

    @ApiOperation("更新联系人")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<Contactor> update(@Validated({Update.class, Default.class}) @RequestBody Contactor contactor) {
        return ResponseDTO.success(contactorManager.update(contactor));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据 id 删除联系人")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        contactorManager.delete(id);
        return ResponseDTO.success();
    }
}
