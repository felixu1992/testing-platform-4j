package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.model.dto.RecordDTO;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.RecordService;
import top.felixu.platform.service.UserProjectService;
import top.felixu.platform.util.UserHolderUtils;
import top.felixu.platform.util.WrapperUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author felixu
 * @since 2021.11.10
 */
@Service
@RequiredArgsConstructor
public class RecordManager {

    private final RecordService recordService;

    private final UserProjectService userProjectService;

    IPage<RecordDTO> page(Record record, PageRequestForm form) {
        boolean condition = UserHolderUtils.getCurrentRole() == RoleTypeEnum.ORDINARY;
        return recordService.page(form.toPage(), WrapperUtils.relation(Wrappers.lambdaQuery(record), Record::getProjectId,
                condition ? userProjectService.getProjectIdsByUserId(UserHolderUtils.getCurrentUserId()) : new ArrayList<>(), condition));
    }
}
