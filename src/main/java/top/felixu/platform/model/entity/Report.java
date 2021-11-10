package top.felixu.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import top.felixu.platform.enums.CaseStatusEnum;
import top.felixu.platform.enums.HttpMethodEnum;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用例测试记录
 *
 * @author felixu
 * @since 2021-08-28
 */
@Data
@TableName(value = "platform_report", autoResultMap = true)
@ApiModel(description = "用例测试记录")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @ApiModelProperty(value = "用例名称")
    private String name;

    @ApiModelProperty(value = "用例描述")
    private String remark;

    @ApiModelProperty(value = "请求方式")
    private HttpMethodEnum method;

    @ApiModelProperty(value = "请求地址")
    private String host;

    @ApiModelProperty(value = "请求路径")
    private String path;

    @ApiModelProperty(value = "请求参数")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;

    @ApiModelProperty(value = "请求头")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> headers;

    @ApiModelProperty(value = "接口依赖")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Dependency> dependencies;

    @ApiModelProperty(value = "校验预期")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Expected> expects;

    @ApiModelProperty(value = "校验预期的状态码")
    private Integer expectedHttpStatus;

    @ApiModelProperty(value = "是否校验状态码")
    private Boolean checkStatus;

    @ApiModelProperty(value = "是否运行")
    private Boolean run;

    @ApiModelProperty(value = "开发者")
    private Integer developer;

    @ApiModelProperty(value = "项目 id")
    private Integer projectId;

    @ApiModelProperty(value = "序号")
    private Integer sort;

    @ApiModelProperty(value = "延迟时长")
    private Integer delay;

    @ApiModelProperty(value = "返回值示例")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String sample;

    @ApiModelProperty(value = "用例分类")
    private Integer groupId;

    @ApiModelProperty(value = "响应的状态码")
    private Integer httpStatus;

    @ApiModelProperty(value = "响应的内容")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> responseContent;

    @ApiModelProperty(value = "请求耗时")
    private Integer timeUsed;

    @ApiModelProperty(value = "测试报告 id")
    private Integer recordId;

    @ApiModelProperty(value = "用例 id")
    private Integer caseId;

    @ApiModelProperty(value = "用例校验结果")
    private CaseStatusEnum status;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已经执行过")
    private boolean executed;

    @ApiModelProperty(value = "拥有者")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private Integer owner;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private Integer createdBy;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updatedBy;

}
