package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author felixu
 * @since 2021.09.09
 */
@Data
@ApiModel("用例复制")
public class CaseCopyForm {

    @NotNull
    @ApiModelProperty("需要复制的用例 id")
    private Integer id;

    @NotBlank
    @ApiModelProperty("新用例的名称")
    private String name;
}