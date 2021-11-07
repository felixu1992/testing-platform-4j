package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.enums.CaseStatusEnum;
import top.felixu.platform.enums.SortEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.form.CaseCopyForm;
import top.felixu.platform.model.form.CaseExecuteForm;
import top.felixu.platform.model.form.CaseSortForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.CaseInfoService;
import top.felixu.platform.service.ContactorService;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.RecordService;
import top.felixu.platform.service.ReportService;
import top.felixu.platform.util.ExecuteCaseUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    private final RecordService recordService;

    private final ReportService reportService;

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

    public CaseInfo copy(CaseCopyForm form) {
        CaseInfo original = caseInfoService.getCaseInfoByIdAndCheck(form.getId());
        CaseInfo caseInfo = BeanUtils.map(original, CaseInfo.class);
        caseInfo.setId(null);
        caseInfo.setCreatedAt(null);
        caseInfo.setCreatedBy(null);
        caseInfo.setUpdatedAt(null);
        caseInfo.setUpdatedBy(null);
        return create(caseInfo);
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
            throw new PlatformException(ErrorCode.CASE_DRAG_MISS_TARGET);
        CaseInfo source = caseInfoService.getCaseInfoByIdAndCheck(form.getSource());
        List<CaseInfo> caseInfos = caseInfoService.listByProjectId(source.getProjectId());
        CaseInfo target;
        switch (form.getOperation()) {
            case TOP:
                target = caseInfos.get(0);
                break;
            case BOTTOM:
                target = caseInfos.get(caseInfos.size() - 1);
                break;
            case UP:
                target = caseInfos.get(caseInfos.indexOf(source) - 1);
                break;
            case DOWN:
                target = caseInfos.get(caseInfos.indexOf(source) + 1);
                break;
            case DRAG:
                target = caseInfoService.getCaseInfoByIdAndCheck(form.getTarget());
                break;
            default:
                throw new PlatformException(ErrorCode.CASE_MOVE_OPERATION_ERROR);
        }
        caseInfos.remove(source);
        if (form.getOperation() == SortEnum.BOTTOM)
            caseInfos.add(source);
        else
            caseInfos.add(caseInfos.indexOf(target), source);
        for (int i = 0; i < caseInfos.size(); i++) {
            caseInfos.get(i).setSort(i + 1);
        }
        caseInfoService.batchUpdate(caseInfos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Record execute(CaseExecuteForm form) {
        // 查询所属项目
        Project project = projectService.getProjectByIdAndCheck(form.getProjectId());
        // 查询项目下所有用例
        List<CaseInfo> cases = caseInfoService.listByProjectId(form.getProjectId());
        List<CaseInfo> caseInfos;
        // 如果有用例，执行单个用例
        if (form.getCaseId() != null)
            caseInfos = cases.stream().filter(caseInfo -> caseInfo.getId().equals(form.getCaseId())).collect(Collectors.toList());
        // 如果有分组，按分组查
        else if (form.getGroupId() != null)
            caseInfos = cases.stream().filter(caseInfo -> caseInfo.getGroupId().equals(form.getGroupId())).collect(Collectors.toList());
        else
            caseInfos = cases;
        // 执行用例得到结果集
        List<Report> reports = ExecuteCaseUtils.execute(project, cases, caseInfos);
        // 创建记录
        Record record = new Record();
        record.setGroupId(project.getGroupId());
        record.setProjectId(project.getId());
        record.setRemark(project.getRemark());
        record.setPassed((int) reports.stream().filter(report -> report.getStatus() == CaseStatusEnum.PASSED).count());
        record.setFailed((int) reports.stream().filter(report -> report.getStatus() == CaseStatusEnum.FAILED).count());
        record.setIgnored((int) reports.stream().filter(report -> report.getStatus() == CaseStatusEnum.IGNORED).count());
        record.setTotal(reports.size());
        // 存储结果和记录
        recordService.save(record);
        reportService.saveBatch(reports);
        return record;
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
        if (caseInfo.getDeveloper() != null)
            contactorService.getContactorByIdAndCheck(caseInfo.getDeveloper());
        // 校验邮箱
        CaseInfo name = caseInfoService.getOne(Wrappers.<CaseInfo>lambdaQuery()
                .eq(CaseInfo::getProjectId, caseInfo.getProjectId()).eq(CaseInfo::getName, caseInfo.getName()));
        if (name != null && (caseInfo.getId() == null || !caseInfo.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.CASE_DUPLICATE_NAME);
        // TODO: 09/08 校验依赖是否成环
        // TODO 校验依赖结构是否完整
    }
}