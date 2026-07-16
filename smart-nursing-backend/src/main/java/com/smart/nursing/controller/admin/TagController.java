package com.smart.nursing.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.TagEntity;
import com.smart.nursing.service.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理 Controller（管理端）
 */
@Tag(name = "标签管理", description = "护理内容标签的增删改查")
@RestController
@RequestMapping("/admin/tag")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @Operation(summary = "获取全部标签")
    @GetMapping("/all")
    public CommonResult<List<TagEntity>> all() {
        return CommonResult.success(tagService.getAllTags());
    }

    @Operation(summary = "分页查询标签")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestParam(defaultValue = "1") Integer pageNo,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) String tagName) {
        Page<TagEntity> page = new Page<>(pageNo, pageSize);
        return CommonResult.successPageData(tagService.listTagByCondition(page, tagName));
    }

    @RecordLog("新增标签")
    @Operation(summary = "新增标签")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody TagEntity entity) {
        tagService.addTag(entity);
        return CommonResult.success();
    }

    @RecordLog("修改标签")
    @Operation(summary = "修改标签")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody TagEntity entity) {
        tagService.updateTag(entity);
        return CommonResult.success();
    }

    @RecordLog("删除标签")
    @Operation(summary = "删除标签")
    @PostMapping("/delete/{tagId}")
    public CommonResult<Void> delete(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return CommonResult.success();
    }
}
