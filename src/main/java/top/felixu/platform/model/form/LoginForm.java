package top.felixu.platform.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录
 *
 * @author felixu
 * @since 2021.08.24
 */
@Data
@ApiModel(description = "登录")
public class LoginForm {

    @NotBlank
    @ApiModelProperty("邮箱")
    private String email;

    @NotBlank
    @ApiModelProperty("密码")
    private String password;
}
