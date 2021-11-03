package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.validation.CaseExecute;

import javax.validation.constraints.NotNull;

/**
 * @author felixu
 * @since 2021.09.09
 */
@Data
@ApiModel("用例执行")
public class CaseExecuteForm {

    @NotNull(groups = CaseExecute.class)
    @ApiModelProperty("用例 id")
    private Integer caseId;

    @NotNull
    @ApiModelProperty("项目 id")
    private Integer projectId;

    @ApiModelProperty("分组 id")
    private Integer groupId;
}
