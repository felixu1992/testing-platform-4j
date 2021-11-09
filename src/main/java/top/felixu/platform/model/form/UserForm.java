package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.entity.User;

import java.util.List;

/**
 * @author felixu
 * @since 2021.11.09
 */
@Data
public class UserForm extends User {

    @ApiModelProperty("用户关联的项目")
    private List<Integer> projectIds;
}
