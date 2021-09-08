package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.enums.SortEnum;

import javax.validation.constraints.NotNull;

/**
 * @author felixu
 * @since 2021.09.08
 */
@Data
@ApiModel("用例排序")
public class CaseSortForm {

    @NotNull
    @ApiModelProperty("鼠标起点")
    private Integer source;

    @ApiModelProperty("鼠标落点")
    private Integer target;

    @NotNull
    @ApiModelProperty("操作类型")
    private SortEnum operation;
}
