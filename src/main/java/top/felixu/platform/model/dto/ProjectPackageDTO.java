package top.felixu.platform.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.CaseInfoGroup;

import java.util.List;

/**
 * @author : zhan9yn
 * @version : 1.0
 * @description : 项目打包导出的DTO
 * @date : 2021/11/8 2:06 下午
 */
@Data
@AllArgsConstructor
public class ProjectPackageDTO {
    private CaseInfoGroup caseInfoGroup;
    private List<CaseInfo> caseInfo;
}
