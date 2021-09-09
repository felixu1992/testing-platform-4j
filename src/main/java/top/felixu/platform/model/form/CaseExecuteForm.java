package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author felixu
 * @since 2021.09.09
 */
@Data
@ApiModel("用例执行")
public class CaseExecuteForm {

    private Integer caseId;

    private Integer projectId;

    private Integer groupId;
}
