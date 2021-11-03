package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author felixu
 * @since 2021.11.03
 */
@Data
@ApiModel("项目复制")
public class ProjectCopyForm {

    @NotNull
    @ApiModelProperty("需要复制的项目 id")
    private Integer id;

    @NotBlank
    @ApiModelProperty("新项目的名称")
    private String name;
}
