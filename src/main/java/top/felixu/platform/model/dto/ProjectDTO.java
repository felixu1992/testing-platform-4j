package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.entity.Project;

/**
 * @author felixu
 * @since 2021.11.05
 */
@Data
public class ProjectDTO extends Project {

    @ApiModelProperty("分组名称")
    private String groupName;
}
