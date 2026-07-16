package com.smart.nursing.controller.admin;

import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.ExamRecordDto;
import com.smart.nursing.service.IExamRecordService;
import com.smart.nursing.vo.ExamResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 考试记录 Controller（管理端）
 */
@Tag(name = "成绩管理", description = "考试记录与成绩查询")
@RestController
@RequestMapping("/admin/examRecord")
@RequiredArgsConstructor
public class ExamRecordController {

    private final IExamRecordService examRecordService;

    @Operation(summary = "分页查询考试记录（成绩列表）")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody ExamRecordDto dto) {
        return CommonResult.successPageData(examRecordService.listRecordByCondition(dto));
    }

    @Operation(summary = "获取成绩详情")
    @GetMapping("/detail/{recordId}")
    public CommonResult<ExamResultVo> detail(@PathVariable Long recordId) {
        return CommonResult.success(examRecordService.getExamResult(recordId));
    }
}
