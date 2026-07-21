package com.smart.nursing.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.MenuEntity;
import com.smart.nursing.entity.RoleEntity;
import com.smart.nursing.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller（管理端）
 */
@Tag(name = "角色管理", description = "角色的增删改查与菜单分配")
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class    RoleController {

    private final IRoleService roleService;

    @Operation(summary = "分页查询角色")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestParam(defaultValue = "1") Integer pageNo,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) String roleName) {
        Page<RoleEntity> page = new Page<>(pageNo, pageSize);
        return CommonResult.successPageData(roleService.listRoleByCondition(page, roleName));
    }

    @RecordLog("新增角色")
    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody RoleEntity entity) {
        roleService.save(entity);
        return CommonResult.success();
    }

    @RecordLog("修改角色")
    @Operation(summary = "修改角色")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody RoleEntity entity) {
        roleService.updateById(entity);
        return CommonResult.success();
    }

    @RecordLog("删除角色")
    @Operation(summary = "删除角色")
    @PostMapping("/delete/{roleId}")
    public CommonResult<Void> delete(@PathVariable Long roleId) {
        roleService.removeById(roleId);
        return CommonResult.success();
    }

    @Operation(summary = "根据ID获取角色详情")
    @GetMapping("/getById/{roleId}")
    public CommonResult<RoleEntity> getById(@PathVariable Long roleId) {
        return CommonResult.success(roleService.getById(roleId));
    }

    @RecordLog("分配菜单")
    @Operation(summary = "给角色分配菜单")
    @PostMapping("/assignMenus")
    public CommonResult<Void> assignMenus(@RequestParam Long roleId,
                                          @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return CommonResult.success();
    }

    @Operation(summary = "获取角色拥有的菜单")
    @GetMapping("/menus/{roleId}")
    public CommonResult<List<MenuEntity>> menus(@PathVariable Long roleId) {
        return CommonResult.success(roleService.getRoleMenus(roleId));
    }
}
