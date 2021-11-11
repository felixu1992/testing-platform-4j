package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PathVariable;
import top.felixu.platform.model.dto.RecordDTO;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.entity.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.service.manager.RecordManager;

/**
 * 用例测试记录
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

    @GetMapping("/{id}")
    public RespDTO<Record> get(@PathVariable Integer id) {
        return RespDTO.success(recordManager.getRecordById(id));
    }

    @GetMapping
    public RespDTO<IPage<RecordDTO>> page(Record record, PageRequestForm form) {
        return RespDTO.success(recordManager.page(record, form));
    }
//
//    @DeleteMapping("/{id}")
//    public boolean delete(@PathVariable Long id) {
//        return reportService.removeById(id);
//    }
}
