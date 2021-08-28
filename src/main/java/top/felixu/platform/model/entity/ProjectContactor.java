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
 * 项目和需要通知的联系人关联表
 *
 * @author felixu
 * @since 2021-08-28
 */
@Data
@TableName("platform_project_contactor")
@ApiModel(description = "项目和需要通知的联系人关联表")
public class ProjectContactor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    private Integer id;

    @ApiModelProperty(value = "项目 id")
    @NotNull
    private Integer projectId;

    @ApiModelProperty(value = "联系人 id")
    @NotNull
    private Integer contactorId;

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
