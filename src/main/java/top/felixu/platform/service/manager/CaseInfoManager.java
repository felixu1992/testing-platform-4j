package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.enums.CaseStatusEnum;
import top.felixu.platform.enums.SortEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.CaseInfoDTO;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.CaseInfoGroup;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.Report;
import top.felixu.platform.model.entity.Record;
import top.felixu.platform.model.form.CaseCopyForm;
import top.felixu.platform.model.form.CaseExecuteForm;
import top.felixu.platform.model.form.CaseSortForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.CaseInfoGroupService;
import top.felixu.platform.service.CaseInfoService;
import top.felixu.platform.service.ContactorService;
import top.felixu.platform.service.ProjectService;
import top.felixu.platform.service.ReportService;
import top.felixu.platform.service.RecordService;
import top.felixu.platform.util.ExecuteCaseUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final ReportService reportService;

    private final RecordService recordService;

    public CaseInfoDTO getCaseInfoById(Integer id) {
        CaseInfoDTO dto = BeanUtils.map(caseInfoService.getCaseInfoByIdAndCheck(id), CaseInfoDTO.class);
        dto.setProjectName(projectService.getProjectByIdAndCheck(dto.getProjectId()).getName());
        dto.setGroupName(caseInfoGroupService.getCaseInfoGroupByIdAndCheck(dto.getGroupId()).getName());
        return dto;
    }

    public IPage<CaseInfoDTO> page(CaseInfo caseInfo, PageRequestForm form) {
        if (caseInfo.getProjectId() == null)
            throw new PlatformException(ErrorCode.PROJECT_MUST_BE_NOT_NULL);
        Project project = projectService.getProjectByIdAndCheck(caseInfo.getProjectId());
        Page<CaseInfo> page = caseInfoService.page(form.toPage(), Wrappers.lambdaQuery(caseInfo).orderByAsc(CaseInfo::getSort));
        Map<Integer, String> groupMap = caseInfoGroupService.getCaseInfoGroupList()
                .stream()
                .collect(Collectors.toMap(CaseInfoGroup::getId, CaseInfoGroup::getName));
        return page.convert(item -> {
            CaseInfoDTO dto = BeanUtils.map(item, CaseInfoDTO.class);
            dto.setProjectName(project.getName());
            dto.setGroupName(groupMap.get(dto.getGroupId()));
            return dto;
        });
    }

    public CaseInfo create(CaseInfo caseInfo) {
        check(caseInfo);
        caseInfo.setSort(getNextSort(caseInfo.getProjectId()));
        return caseInfoService.create(caseInfo);
    }

    public CaseInfo copy(CaseCopyForm form) {
        CaseInfo original = caseInfoService.getCaseInfoByIdAndCheck(form.getId());
        CaseInfo caseInfo = BeanUtils.map(original, CaseInfo.class);
        caseInfo.setName(form.getName());
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
         * 3. 置顶：移出 source，插入最前面
         * 4. 置底：移出 source，插入最后面
         * 5. 上下移动：交换 source 和 target 位置
         * 6. 拖动：移出 source，插入到 target 位置前面
         * 7. 重新计算所有 sort，批量更新
         */
        if (form.getOperation() == SortEnum.DRAG && form.getTarget() == null)
            throw new PlatformException(ErrorCode.CASE_DRAG_MISS_TARGET);
        CaseInfo source = caseInfoService.getCaseInfoByIdAndCheck(form.getSource());
        List<CaseInfo> caseInfos = caseInfoService.listByProjectId(source.getProjectId());
        CaseInfo target;
        switch (form.getOperation()) {
            // 置顶，需要注意是否已经是第一个了
            case TOP:
                target = caseInfos.get(0);
                // 点击的就是第一个
                if (form.getSource().equals(target.getId()))
                    return;
                // 移除起点元素
                caseInfos.remove(source);
                // 将起点元素插入到最前面
                caseInfos.add(0, source);
                break;
            // 置底，需要注意是否已经是最后了
            case BOTTOM:
                target = caseInfos.get(caseInfos.size() - 1);
                // 点击的就是最后一个
                if (form.getSource().equals(target.getId()))
                    return;
                // 移除起点元素
                caseInfos.remove(source);
                // 将起点元素插入到最后面
                caseInfos.add(caseInfos.size() - 1, source);
                break;
            // 上移，注意是否是最上面的
            case UP:
                int upIndex = caseInfos.indexOf(source);
                // 最上面了，不用操作
                if (upIndex ==0)
                    return;
                target = caseInfos.get(upIndex - 1);
                // 交换位置
                Collections.swap(caseInfos, caseInfos.indexOf(source), caseInfos.indexOf(target));
                break;
            // 下移，注意是否为最下面的
            case DOWN:
                int downIndex = caseInfos.indexOf(source);
                // 最下面了，不用操作
                if (downIndex == caseInfos.size() - 1)
                    return;
                target = caseInfos.get(downIndex + 1);
                // 交换位置
                Collections.swap(caseInfos, caseInfos.indexOf(source), caseInfos.indexOf(target));
                break;
            // 拖动，直接查出目标位置
            case DRAG:
                // 如果两个位置相同，不操作
                if (form.getSource().equals(form.getTarget()))
                    return;
                target = caseInfoService.getCaseInfoByIdAndCheck(form.getTarget());
                // 移除起点元素
                caseInfos.remove(source);
                // 将起点元素插入到落点元素到前面
                caseInfos.add(caseInfos.indexOf(target), source);
                break;
            default:
                throw new PlatformException(ErrorCode.CASE_MOVE_OPERATION_ERROR);
        }
        // 重新构建顺序
        for (int i = 0; i < caseInfos.size(); i++) {
            caseInfos.get(i).setSort(i + 1);
        }
        caseInfoService.batchUpdate(caseInfos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Report execute(CaseExecuteForm form) {
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
        List<Record> records = ExecuteCaseUtils.execute(project, cases, caseInfos);
        // 创建记录
        Report record1 = new Report();
        record1.setGroupId(project.getGroupId());
        record1.setProjectId(project.getId());
        record1.setRemark(project.getRemark());
        record1.setPassed((int) records.stream().filter(report -> report.getStatus() == CaseStatusEnum.PASSED).count());
        record1.setFailed((int) records.stream().filter(report -> report.getStatus() == CaseStatusEnum.FAILED).count());
        record1.setIgnored((int) records.stream().filter(report -> report.getStatus() == CaseStatusEnum.IGNORED).count());
        record1.setTotal(records.size());
        // 存储结果和记录
        reportService.save(record1);
        records.forEach(report -> report.setRecordId(record1.getId()));
        recordService.saveBatch(records);
        return record1;
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
        // 校验名称是否重复
        CaseInfo name = caseInfoService.getOne(Wrappers.<CaseInfo>lambdaQuery()
                .eq(CaseInfo::getProjectId, caseInfo.getProjectId()).eq(CaseInfo::getName, caseInfo.getName()));
        if (name != null && (caseInfo.getId() == null || !caseInfo.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.CASE_DUPLICATE_NAME);
        // TODO: 09/08 校验依赖是否成环
        // TODO 校验依赖结构是否完整
    }
}