package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.ExamDto;
import com.smart.nursing.entity.ExamEntity;
import com.smart.nursing.service.IExamService;
import com.smart.nursing.vo.ExamDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试管理 Controller（管理端）
 */
@Tag(name = "考试管理", description = "考试的增删改查与发布")
@RestController
@RequestMapping("/admin/exam")
@RequiredArgsConstructor
public class ExamController {

    private final IExamService examService;

    @Operation(summary = "分页查询考试")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody ExamDto dto) {
        return CommonResult.successPageData(examService.listExamByCondition(dto));
    }

    @Operation(summary = "根据ID获取考试详情（含试题）")
    @GetMapping("/getById/{examId}")
    public CommonResult<ExamDetailVo> getById(@PathVariable Long examId) {
        return CommonResult.success(examService.getExamDetail(examId));
    }

    @RecordLog("新增考试")
    @Operation(summary = "新增考试")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody ExamEntity entity) {
        examService.addExam(entity);
        return CommonResult.success();
    }

    @RecordLog("修改考试")
    @Operation(summary = "修改考试")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody ExamEntity entity) {
        examService.updateExam(entity);
        return CommonResult.success();
    }

    @RecordLog("删除考试")
    @Operation(summary = "删除考试")
    @PostMapping("/delete/{examId}")
    public CommonResult<Void> delete(@PathVariable Long examId) {
        examService.deleteExam(examId);
        return CommonResult.success();
    }

    @RecordLog("发布考试")
    @Operation(summary = "发布/结束考试")
    @PostMapping("/publish/{examId}")
    public CommonResult<Void> publish(@PathVariable Long examId) {
        examService.publishExam(examId);
        return CommonResult.success();
    }

    @RecordLog("考试组卷")
    @Operation(summary = "考试关联试题（组卷）")
    @PostMapping("/assignQuestions")
    public CommonResult<Void> assignQuestions(@RequestParam Long examId,
                                              @RequestBody List<Long> questionIds) {
        examService.assignQuestions(examId, questionIds);
        return CommonResult.success();
    }
}
