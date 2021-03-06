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
import top.felixu.platform.service.UserProjectService;
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
        CaseInfo caseInfo = caseInfoService.getCaseInfoByIdAndCheck(id);
        projectService.getProjectByIdAndCheck(id);
        caseInfoService.delete(caseInfo);
    }

    public void sort(CaseSortForm form) {
        /*
         * 1. ?????????????????????????????? target
         * 2. ?????????????????? target
         * 3. ??????????????? source??????????????????
         * 4. ??????????????? source??????????????????
         * 5. ????????????????????? source ??? target ??????
         * 6. ??????????????? source???????????? target ????????????
         * 7. ?????????????????? sort???????????????
         */
        if (form.getOperation() == SortEnum.DRAG && form.getTarget() == null)
            throw new PlatformException(ErrorCode.CASE_DRAG_MISS_TARGET);
        CaseInfo source = caseInfoService.getCaseInfoByIdAndCheck(form.getSource());
        projectService.getProjectByIdAndCheck(source.getProjectId());
        List<CaseInfo> caseInfos = caseInfoService.listByProjectId(source.getProjectId());
        CaseInfo target;
        switch (form.getOperation()) {
            // ????????????????????????????????????????????????
            case TOP:
                target = caseInfos.get(0);
                // ????????????????????????
                if (form.getSource().equals(target.getId()))
                    return;
                // ??????????????????
                caseInfos.remove(source);
                // ?????????????????????????????????
                caseInfos.add(0, source);
                break;
            // ?????????????????????????????????????????????
            case BOTTOM:
                target = caseInfos.get(caseInfos.size() - 1);
                // ???????????????????????????
                if (form.getSource().equals(target.getId()))
                    return;
                // ??????????????????
                caseInfos.remove(source);
                // ?????????????????????????????????
                caseInfos.add(caseInfos.size() - 1, source);
                break;
            // ????????????????????????????????????
            case UP:
                int upIndex = caseInfos.indexOf(source);
                // ???????????????????????????
                if (upIndex ==0)
                    return;
                target = caseInfos.get(upIndex - 1);
                // ????????????
                Collections.swap(caseInfos, caseInfos.indexOf(source), caseInfos.indexOf(target));
                break;
            // ????????????????????????????????????
            case DOWN:
                int downIndex = caseInfos.indexOf(source);
                // ???????????????????????????
                if (downIndex == caseInfos.size() - 1)
                    return;
                target = caseInfos.get(downIndex + 1);
                // ????????????
                Collections.swap(caseInfos, caseInfos.indexOf(source), caseInfos.indexOf(target));
                break;
            // ?????????????????????????????????
            case DRAG:
                // ????????????????????????????????????
                if (form.getSource().equals(form.getTarget()))
                    return;
                target = caseInfoService.getCaseInfoByIdAndCheck(form.getTarget());
                // ??????????????????
                caseInfos.remove(source);
                // ?????????????????????????????????????????????
                caseInfos.add(caseInfos.indexOf(target), source);
                break;
            default:
                throw new PlatformException(ErrorCode.CASE_MOVE_OPERATION_ERROR);
        }
        // ??????????????????
        for (int i = 0; i < caseInfos.size(); i++) {
            caseInfos.get(i).setSort(i + 1);
        }
        caseInfoService.batchUpdate(caseInfos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Report execute(CaseExecuteForm form) {
        // ??????????????????
        Project project = projectService.getProjectByIdAndCheck(form.getProjectId());
        // ???????????????????????????
        List<CaseInfo> cases = caseInfoService.listByProjectId(form.getProjectId());
        List<CaseInfo> caseInfos;
        // ????????????????????????????????????
        if (form.getCaseId() != null)
            caseInfos = cases.stream().filter(caseInfo -> caseInfo.getId().equals(form.getCaseId())).collect(Collectors.toList());
        // ??????????????????????????????
        else if (form.getGroupId() != null)
            caseInfos = cases.stream().filter(caseInfo -> caseInfo.getGroupId().equals(form.getGroupId())).collect(Collectors.toList());
        else
            caseInfos = cases;
        // ???????????????????????????
        List<Record> records = ExecuteCaseUtils.execute(project, cases, caseInfos);
        // ????????????
        Report record1 = new Report();
        record1.setGroupId(project.getGroupId());
        record1.setProjectId(project.getId());
        record1.setRemark(project.getRemark());
        record1.setPassed((int) records.stream().filter(report -> report.getStatus() == CaseStatusEnum.PASSED).count());
        record1.setFailed((int) records.stream().filter(report -> report.getStatus() == CaseStatusEnum.FAILED).count());
        record1.setIgnored((int) records.stream().filter(report -> report.getStatus() == CaseStatusEnum.IGNORED).count());
        record1.setTotal(records.size());
        // ?????????????????????
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
        // ????????????????????????
        projectService.getProjectByIdAndCheck(caseInfo.getProjectId());
        // ????????????????????????
        caseInfoGroupService.getCaseInfoGroupByIdAndCheck(caseInfo.getGroupId());
        // ???????????????????????????
        if (caseInfo.getDeveloper() != null)
            contactorService.getContactorByIdAndCheck(caseInfo.getDeveloper());
        // ????????????????????????
        CaseInfo name = caseInfoService.getOne(Wrappers.<CaseInfo>lambdaQuery()
                .eq(CaseInfo::getProjectId, caseInfo.getProjectId()).eq(CaseInfo::getName, caseInfo.getName()));
        if (name != null && (caseInfo.getId() == null || !caseInfo.getId().equals(name.getId())))
            throw new PlatformException(ErrorCode.CASE_DUPLICATE_NAME);
        // TODO: 09/08 ????????????????????????
        // TODO ??????????????????????????????
    }
}