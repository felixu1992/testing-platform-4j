package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.model.dto.RecordDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.manager.RecordManager;

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

    private final RecordManager recordManager;

    @GetMapping
    @ApiOperation("分页查询用例执行记录")
    public RespDTO<IPage<RecordDTO>> page(Record record, PageRequestForm form) {
        return RespDTO.success(recordManager.page(record, form));
    }

//    @DeleteMapping("/{id}")
//    public boolean delete(@PathVariable Long id) {
//        return recordService.removeById(id);
//    }
}
