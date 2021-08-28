package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.felixu.platform.service.ContactorGroupService;

import javax.validation.groups.Default;

/**
 * 联系人分组
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contactor-group")
public class ContactorGroupController {

    private final ContactorGroupService contactorGroupService;

    @GetMapping("/{id}")
    public ContactorGroup get(@PathVariable Long id) {
        return contactorGroupService.getById(id);
    }

    @GetMapping
    public IPage<ContactorGroup> page(ContactorGroup contactorGroup, PageRequestForm form) {
        return contactorGroupService.page(form.toPage(), new QueryWrapper<>(contactorGroup));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody ContactorGroup contactorGroup) {
        return contactorGroupService.save(contactorGroup);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody ContactorGroup contactorGroup) {
        return contactorGroupService.updateById(contactorGroup);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return contactorGroupService.removeById(id);
    }
}
