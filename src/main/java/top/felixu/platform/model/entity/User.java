package top.felixu.platform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
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
@ApiModel(description = "用户")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    private String password;

    private String avatar;

    @ApiModelProperty("上级管理员 id")
    private Integer parentId;

    @Null(groups = Create.class)
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private RoleTypeEnum role;

    private String secret;

    @TableField(exist = false)
    private String token;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
