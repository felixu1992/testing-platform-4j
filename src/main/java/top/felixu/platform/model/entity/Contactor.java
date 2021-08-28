package top.felixu.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import javax.validation.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 联系人
 *
 * @author felixu
 * @since 2021-08-28
 */
@Data
@TableName("platform_contactor")
@ApiModel(description = "联系人")
public class Contactor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @ApiModelProperty(value = "联系人名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "邮箱")
    @NotBlank
    private String email;

    @ApiModelProperty(value = "手机号")
    @NotBlank
    private String phone;

    @ApiModelProperty(value = "分组 id")
    @NotNull
    private Integer groupId;

    @ApiModelProperty(value = "拥有者")
    @NotNull
    private Integer owner;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "创建者")
    @NotNull
    private Integer createdBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "更新者")
    @NotNull
    private Integer updatedBy;

}
