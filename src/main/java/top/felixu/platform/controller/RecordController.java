package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.RecordDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.model.entity.Record;
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
import top.felixu.platform.service.RecordService;

import javax.validation.groups.Default;

/**
 * 测试报告
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "用例执行记录管理")
@RequestMapping("/api/record")
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    @ApiOperation("分页查询用例执行记录")
    public RespDTO<IPage<RecordDTO>> page(Record record, PageRequestForm form) {
        return RespDTO.success();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean create(@Validated({Create.class, Default.class}) @RequestBody Record record) {
        return recordService.save(record);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return recordService.removeById(id);
    }
}
