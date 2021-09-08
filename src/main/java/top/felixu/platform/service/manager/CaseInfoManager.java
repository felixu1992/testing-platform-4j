package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.platform.enums.SortEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Contactor;
import top.felixu.platform.model.form.CaseSortForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.CaseInfoService;
import top.felixu.platform.service.ContactorService;
import top.felixu.platform.service.ProjectService;

import java.util.Objects;

/**
 * @author felixu
 * @since 2021.09.07
 */
@Service
@RequiredArgsConstructor
public class CaseInfoManager {

    private final CaseInfoService caseInfoService;

    private final ProjectService projectService;

    private final CaseInfoGroupService caseInfoGroupService;

    private final ContactorService contactorService;

    public CaseInfo getCaseInfoById(Integer id) {
        return caseInfoService.getCaseInfoByIdAndCheck(id);
    }

    public IPage<CaseInfo> page(CaseInfo caseInfo, PageRequestForm form) {
        return caseInfoService.page(form.toPage(), Wrappers.lambdaQuery(caseInfo));
    }

    public CaseInfo create(CaseInfo caseInfo) {
        check(caseInfo);
        caseInfo.setSort(getNextSort(caseInfo.getProjectId()));
        return caseInfoService.create(caseInfo);
    }

    public CaseInfo update(CaseInfo caseInfo) {
        check(caseInfo);
        return caseInfoService.update(caseInfo);
    }

    public void delete(Integer id) {
        caseInfoService.delete(caseInfoService.getCaseInfoByIdAndCheck(id));
    }

    public void sort(CaseSortForm form) {
        /*
         * 1. 如果是拖动，必须要有 target
         * 2. 其他情况计算 target
         * 3. 将 source 从列表中移出，然后插入到 target 前面
         * 4. 重新计算所有 sort，批量更新
         */
        if (form.getOperation() == SortEnum.DRAG && form.getTarget() == null)
            throw new PlatformException(ErrorCode.CASE_DRAG_TARGET);

    }

    private int getNextSort(Integer projectId) {
        CaseInfo maxSort = caseInfoService.getOne(Wrappers.<CaseInfo>lambdaQuery()
                .eq(CaseInfo::getProjectId, projectId)
                .orderByDesc(CaseInfo::getSort).last("limit 1"));
        if (maxSort == null)
            return 1;
        return maxSort.getSort() + 1;
    }

    private void check(CaseInfo caseInfo) {
        // 判断项目是否存在
        projectService.getProjectByIdAndCheck(caseInfo.getProjectId());
        // 判断分组是否存在
        caseInfoGroupService.getCaseInfoGroupByIdAndCheck(caseInfo.getGroupId());
        // 判断开发者是否存在
        if (caseInfo.getDeveloper() == null)
            contactorService.getContactorByIdAndCheck(caseInfo.getDeveloper());
        // 校验邮箱
        CaseInfo name = caseInfoService.getOne(Wrappers.<CaseInfo>lambdaQuery()
                .eq(CaseInfo::getProjectId, caseInfo.getProjectId()).eq(CaseInfo::getName, caseInfo.getName()));
        if (name != null && (caseInfo.getId() == null || !caseInfo.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.CASE_DUPLICATE_NAME);
        // TODO: 09/08 校验依赖是否成环
    }
}