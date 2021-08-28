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
 * 用例信息
 *
 * @author felixu
 * @since 2021-08-28
 */
@Data
@TableName("platform_case_info")
@ApiModel(description = "用例信息")
public class CaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @ApiModelProperty(value = "用例名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "用例描述")
    private String remark;

    @ApiModelProperty(value = "请求方式")
    @NotBlank
    private String method;

    @ApiModelProperty(value = "请求地址")
    private String host;

    @ApiModelProperty(value = "请求路径")
    @NotBlank
    private String path;

    @ApiModelProperty(value = "请求参数")
    private String params;

    @ApiModelProperty(value = "请求头")
    private String headers;

    @ApiModelProperty(value = "需要依赖的键")
    private String extendKeys;

    @ApiModelProperty(value = "需要依赖的值")
    private String extendValues;

    @ApiModelProperty(value = "校验预期的键")
    private String expectedKeys;

    @ApiModelProperty(value = "校验预期的值")
    private String expectedValues;

    @ApiModelProperty(value = "校验预期的状态码")
    private Integer expectedHttpStatus;

    @ApiModelProperty(value = "是否校验状态码")
    @NotNull
    private Boolean checkStatus;

    @ApiModelProperty(value = "是否运行")
    @NotNull
    private Boolean run;

    @ApiModelProperty(value = "开发者")
    private Integer developer;

    @ApiModelProperty(value = "项目 id")
    @NotNull
    private Integer projectId;

    @ApiModelProperty(value = "序号")
    @NotNull
    private Integer sort;

    @ApiModelProperty(value = "延迟时长")
    @NotNull
    private Integer delay;

    @ApiModelProperty(value = "返回值示例")
    private String sample;

    @ApiModelProperty(value = "用例分类")
    @NotNull
    private Integer groupId;

    @ApiModelProperty(value = "所有者")
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
