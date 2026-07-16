package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.UserDto;
import com.smart.nursing.entity.UserEntity;
import com.smart.nursing.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller（管理端）
 */
@Tag(name = "用户管理", description = "用户的增删改查与角色分配")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "分页查询用户")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody UserDto dto) {
        return CommonResult.successPageData(userService.listUserByCondition(dto));
    }

    @RecordLog("新增用户")
    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody UserDto dto) {
        userService.addUser(dto);
        return CommonResult.success();
    }

    @RecordLog("修改用户")
    @Operation(summary = "修改用户")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody UserDto dto) {
        userService.updateUser(dto);
        return CommonResult.success();
    }

    @RecordLog("删除用户")
    @Operation(summary = "删除用户")
    @PostMapping("/delete/{userId}")
    public CommonResult<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return CommonResult.success();
    }

    @Operation(summary = "根据ID获取用户详情")
    @GetMapping("/getById/{userId}")
    public CommonResult<UserEntity> getById(@PathVariable Long userId) {
        return CommonResult.success(userService.getById(userId));
    }

    @RecordLog("分配角色")
    @Operation(summary = "给用户分配角色")
    @PostMapping("/assignRoles")
    public CommonResult<Void> assignRoles(@RequestParam Long userId,
                                          @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return CommonResult.success();
    }
}
