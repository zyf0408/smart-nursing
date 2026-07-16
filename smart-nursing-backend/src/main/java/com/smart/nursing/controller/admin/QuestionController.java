package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.QuestionDto;
import com.smart.nursing.entity.QuestionEntity;
import com.smart.nursing.service.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 试题管理 Controller（管理端）
 */
@Tag(name = "试题管理", description = "试题的增删改查")
@RestController
@RequestMapping("/admin/question")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @Operation(summary = "分页查询试题")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody QuestionDto dto) {
        return CommonResult.successPageData(questionService.listQuestionByCondition(dto));
    }

    @RecordLog("新增试题")
    @Operation(summary = "新增试题")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody QuestionEntity entity) {
        questionService.addQuestion(entity);
        return CommonResult.success();
    }

    @RecordLog("修改试题")
    @Operation(summary = "修改试题")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody QuestionEntity entity) {
        questionService.updateQuestion(entity);
        return CommonResult.success();
    }

    @RecordLog("删除试题")
    @Operation(summary = "删除试题")
    @PostMapping("/delete/{questionId}")
    public CommonResult<Void> delete(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return CommonResult.success();
    }
}
