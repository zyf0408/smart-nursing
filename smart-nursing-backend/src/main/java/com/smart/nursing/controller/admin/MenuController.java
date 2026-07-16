package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.MenuEntity;
import com.smart.nursing.service.IMenuService;
import com.smart.nursing.vo.MenuTreeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller（管理端）
 */
@Tag(name = "菜单管理", description = "菜单的增删改查与树形结构")
@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class MenuController {

    private final IMenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public CommonResult<List<MenuTreeVo>> tree() {
        return CommonResult.success(menuService.getMenuTree());
    }

    @Operation(summary = "获取全部菜单（平铺列表）")
    @GetMapping("/list")
    public CommonResult<List<MenuEntity>> list() {
        return CommonResult.success(menuService.getAllMenus());
    }

    @RecordLog("新增菜单")
    @Operation(summary = "新增菜单")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody MenuEntity entity) {
        menuService.save(entity);
        return CommonResult.success();
    }

    @RecordLog("修改菜单")
    @Operation(summary = "修改菜单")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody MenuEntity entity) {
        menuService.updateById(entity);
        return CommonResult.success();
    }

    @RecordLog("删除菜单")
    @Operation(summary = "删除菜单")
    @PostMapping("/delete/{menuId}")
    public CommonResult<Void> delete(@PathVariable Long menuId) {
        menuService.removeById(menuId);
        return CommonResult.success();
    }
}
