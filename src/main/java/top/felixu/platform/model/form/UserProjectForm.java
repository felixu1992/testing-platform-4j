package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author felixu
 * @since 2021.11.09
 */
@Data
public class UserProjectForm {

    @NotNull
    @ApiModelProperty("用户 id")
    private Integer userId;

    @ApiModelProperty("关联的项目 id 列表")
    private List<Integer> projectIds;
}
