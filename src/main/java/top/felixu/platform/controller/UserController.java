package top.felixu.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.model.dto.ResponseDTO;
import top.felixu.platform.model.entity.User;
import top.felixu.platform.model.form.ChangePasswordForm;
import top.felixu.platform.model.form.LoginForm;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.model.validation.Create;
import top.felixu.platform.model.validation.Update;
import top.felixu.platform.service.UserService;
import top.felixu.platform.service.manager.UserManager;

import javax.validation.groups.Default;

/**
 * 
 *
 * @author felixu
 * @since 2021-08-23
 */
@RestController
@Api(tags = "用户管理")
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserManager userManager;

    @PostMapping("/login")
    @ApiOperation("用户登录，只支持邮箱加密码登录")
    public ResponseDTO<User> login(@Validated @RequestBody LoginForm form) {
        return ResponseDTO.success(userManager.login(form));
    }

    @DeleteMapping("/logout")
    @ApiOperation("用户退出登录状态")
    public ResponseDTO<Void> logout() {
        userManager.logout();
        return ResponseDTO.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询用户详情")
    public ResponseDTO<User> get(@PathVariable Integer id) {
        return ResponseDTO.success(userManager.getUserById(id));
    }

    @GetMapping
    @ApiOperation("分页查询用户列表")
    public ResponseDTO<IPage<User>> page(User user, PageRequestForm form) {
        return ResponseDTO.success(userManager.page(user, form));
    }

    @ApiOperation("创建用户")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<User> create(@Validated({Create.class, Default.class}) @RequestBody User user) {
        return ResponseDTO.success(userManager.create(user));
    }

    @ApiOperation("修改用户信息")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<User> update(@Validated({Update.class, Default.class}) @RequestBody User user) {
        return ResponseDTO.success(userManager.update(user));
    }

    @ApiOperation("重置 secret")
    @PutMapping("/reset/{id}/secret")
    public ResponseDTO<User> resetSecret(@PathVariable Integer id) {
        return ResponseDTO.success(userManager.resetSecret(id));
    }

    @ApiOperation("重置密码")
    @PutMapping("/reset/{id}/password")
    public ResponseDTO<User> resetPassword(@PathVariable Integer id) {
        return ResponseDTO.success(userManager.resetPassword(id));
    }

    @ApiOperation("修改密码")
    @PutMapping("/change/{id}/password")
    public ResponseDTO<User> changePassword(@PathVariable Integer id, @Validated @RequestBody ChangePasswordForm form) {
        return ResponseDTO.success(userManager.changePassword(id, form));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户信息")
    public ResponseDTO<Void> delete(@PathVariable Integer id) {
        userManager.delete(id);
        return ResponseDTO.success();
    }
}
