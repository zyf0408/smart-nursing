package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.ArticleDto;
import com.smart.nursing.service.IArticleService;
import com.smart.nursing.vo.ArticleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文章管理 Controller（管理端）
 */
@Tag(name = "文章管理", description = "护理文章的增删改查")
@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class ArticleController {

    private final IArticleService articleService;

    @Operation(summary = "分页查询文章")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody ArticleDto dto) {
        return CommonResult.successPageData(articleService.listArticleByCondition(dto));
    }

    @Operation(summary = "根据ID获取文章详情")
    @GetMapping("/getById/{articleId}")
    public CommonResult<ArticleVo> getById(@PathVariable Long articleId) {
        return CommonResult.success(articleService.getArticleById(articleId));
    }

    @RecordLog("新增文章")
    @Operation(summary = "新增文章")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody ArticleDto dto) {
        articleService.addArticle(dto);
        return CommonResult.success();
    }

    @RecordLog("修改文章")
    @Operation(summary = "修改文章")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody ArticleDto dto) {
        articleService.updateArticle(dto);
        return CommonResult.success();
    }

    @RecordLog("删除文章")
    @Operation(summary = "删除文章")
    @PostMapping("/delete/{articleId}")
    public CommonResult<Void> delete(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);
        return CommonResult.success();
    }
}
