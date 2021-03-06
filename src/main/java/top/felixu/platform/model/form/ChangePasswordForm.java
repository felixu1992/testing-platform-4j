package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.validation.ChangePassword;

import javax.validation.constraints.NotBlank;

/**
 * @author felixu
 * @since 2021.08.28
 */
@Data
@ApiModel("修改密码")
public class ChangePasswordForm {

    @NotBlank(groups = ChangePassword.class)
    @ApiModelProperty("旧密码")
    private String originalPassword;

    @NotBlank
    @ApiModelProperty("新密码")
    private String newPassword;
}
