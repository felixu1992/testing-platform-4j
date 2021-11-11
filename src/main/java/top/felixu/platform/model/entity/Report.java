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
 * 测试报告
 *
 * @author felixu
 * @since 2021-08-28
 */
@Data
@TableName("platform_report")
@ApiModel(description = "测试报告")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @ApiModelProperty(value = "项目分组 id")
    @NotNull
    private Integer groupId;

    @ApiModelProperty(value = "项目 id")
    @NotNull
    private Integer projectId;

    @ApiModelProperty(value = "项目描述")
    private String remark;

    @ApiModelProperty(value = "通过用例数")
    @NotNull
    private Integer passed;

    @ApiModelProperty(value = "失败用例数")
    @NotNull
    private Integer failed;

    @ApiModelProperty(value = "忽略用例数")
    @NotNull
    private Integer ignored;

    @ApiModelProperty(value = "用例总数")
    @NotNull
    private Integer total;

    @ApiModelProperty(value = "拥有者")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private Integer owner;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private Integer createdBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updatedBy;

}
