package top.felixu.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.enums.RoleTypeEnum;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 *
 * @author felixu
 * @since 2021-08-23
 */
@Data
@TableName("platform_user")
@ApiModel(description = "用户信息")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @ApiModelProperty("主键 id")
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @NotBlank
    @ApiModelProperty("用户名")
    @TableField(condition = SqlCondition.LIKE)
    private String username;

    @NotBlank
    @ApiModelProperty("邮箱")
    @TableField(condition = SqlCondition.LIKE)
    private String email;

    @NotBlank
    @ApiModelProperty("手机号")
    @TableField(condition = SqlCondition.LIKE)
    private String phone;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("上级管理员 id")
    private Integer parentId;

    @ApiModelProperty("角色")
    @Null(groups = Create.class)
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private RoleTypeEnum role;

    @ApiModelProperty("无密码登录密钥")
    private String secret;

    @TableField(exist = false)
    @ApiModelProperty("登录后返回的 token")
    private String token;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
