package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.entity.Report;

/**
 * @author felixu
 * @since 2021.11.07
 */
@Data
public class ReportDTO extends Report {

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("项目分组")
    private String groupName;
}
