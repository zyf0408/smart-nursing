package com.smart.nursing.controller.common;

import com.smart.nursing.aop.NoToken;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.service.ICategoryService;
import com.smart.nursing.vo.CategoryTreeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类公共接口 Controller
 */
@Tag(name = "分类管理", description = "护理内容分类接口")
@RestController
@RequestMapping("/common/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @NoToken
    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public CommonResult<List<CategoryTreeVo>> tree() {
        List<CategoryTreeVo> tree = categoryService.getCategoryTree();
        return CommonResult.success(tree);
    }
}
