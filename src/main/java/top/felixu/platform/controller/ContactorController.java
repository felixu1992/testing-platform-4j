package top.felixu.platform.controller;

import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.service.ContactorService;
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
 * 联系人
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api//contactor")
public class ContactorController {

    private final ContactorService contactorService;

    @GetMapping("/{id}")
    public Contactor get(@PathVariable Long id) {
        return contactorService.getById(id);
    }

    @GetMapping
    public IPage<Contactor> page(Contactor contactor, PageRequestForm form) {
        return contactorService.page(form.toPage(), new QueryWrapper<>(contactor));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody Contactor contactor) {
        return contactorService.save(contactor);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@Validated({Update.class, Default.class}) @RequestBody Contactor contactor) {
        return contactorService.updateById(contactor);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return contactorService.removeById(id);
    }
}
