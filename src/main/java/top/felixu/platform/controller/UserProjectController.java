package top.felixu.platform.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.felixu.platform.model.dto.RespDTO;
import top.felixu.platform.model.form.UserProjectForm;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.felixu.platform.service.manager.UserProjectManager;

/**
 * 用户关联项目的关联表
 *
 * @author felixu
 * @since 2021-08-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "用户和项目关联关系")
@RequestMapping("/api/user-project")
public class UserProjectController {

    private final UserProjectManager userProjectManager;

    @ApiOperation("更新用户和项目的关联关系")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespDTO<Void> updateRelation(@Validated @RequestBody UserProjectForm form) {
        userProjectManager.updateRelation(form);
        return RespDTO.success();
    }
}
