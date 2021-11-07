package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.entity.CaseInfo;

/**
 * @author felixu
 * @since 2021.11.07
 */
@Data
public class CaseInfoDTO extends CaseInfo {

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("分类名称")
    private String groupName;
}
