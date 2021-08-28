package top.felixu.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import javax.validation.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色
 *
 * @author felixu
 * @since 2021-08-28
 */
@Data
@TableName("platform_role")
@ApiModel(description = "角色")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    private Integer id;

    @ApiModelProperty(value = "角色名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "角色标识")
    @NotBlank
    private String role;

    @ApiModelProperty(value = "角色拥有的菜单")
    @NotBlank
    private String menus;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}
