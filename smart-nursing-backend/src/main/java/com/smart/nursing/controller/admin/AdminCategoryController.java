package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.CategoryEntity;
import com.smart.nursing.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理 Controller（管理端）
 */
@Tag(name = "分类管理", description = "护理内容分类的增删改查")
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final ICategoryService categoryService;

    @RecordLog("新增分类")
    @Operation(summary = "新增分类")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody CategoryEntity entity) {
        categoryService.save(entity);
        return CommonResult.success();
    }

    @RecordLog("修改分类")
    @Operation(summary = "修改分类")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody CategoryEntity entity) {
        categoryService.updateById(entity);
        return CommonResult.success();
    }

    @RecordLog("删除分类")
    @Operation(summary = "删除分类")
    @PostMapping("/delete/{categoryId}")
    public CommonResult<Void> delete(@PathVariable Long categoryId) {
        categoryService.removeById(categoryId);
        return CommonResult.success();
    }
}
