package com.smart.nursing.controller.mobile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.nursing.aop.LoginUser;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.ExamEntity;
import com.smart.nursing.entity.ExamRecordEntity;
import com.smart.nursing.service.IExamRecordService;
import com.smart.nursing.service.IExamService;
import com.smart.nursing.vo.ExamDetailVo;
import com.smart.nursing.vo.ExamResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端考试 Controller
 */
@Tag(name = "移动端-考试", description = "考试列表、详情、开始、交卷、结果")
@RestController
@RequestMapping("/mobile/exam")
@RequiredArgsConstructor
public class MobileExamController {

    private final IExamService examService;
    private final IExamRecordService examRecordService;

    @Operation(summary = "可用考试列表")
    @GetMapping("/list")
    public CommonResult<List<ExamEntity>> list() {
        // 查询已发布的考试（status=1）
        List<ExamEntity> exams = examService.list(new LambdaQueryWrapper<ExamEntity>()
                .eq(ExamEntity::getStatus, 1)
                .orderByDesc(ExamEntity::getCreateTime));
        return CommonResult.success(exams);
    }

    @Operation(summary = "考试详情（含试题）")
    @GetMapping("/detail/{examId}")
    public CommonResult<ExamDetailVo> detail(@PathVariable Long examId) {
        return CommonResult.success(examService.getExamDetail(examId));
    }

    @Operation(summary = "开始考试")
    @PostMapping("/start/{examId}")
    public CommonResult<Void> start(HttpServletRequest request, @PathVariable Long examId) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        examRecordService.startExam(examId, loginUser.getUserId());
        return CommonResult.success();
    }

    @Operation(summary = "交卷")
    @PostMapping("/submit")
    public CommonResult<ExamResultVo> submit(HttpServletRequest request,
                                             @RequestParam Long examId,
                                             @RequestParam String answers) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        ExamResultVo result = examRecordService.submitExam(examId, loginUser.getUserId(), answers);
        return CommonResult.success(result);
    }

    @Operation(summary = "考试结果")
    @GetMapping("/result/{recordId}")
    public CommonResult<ExamResultVo> result(@PathVariable Long recordId) {
        return CommonResult.success(examRecordService.getExamResult(recordId));
    }

    @Operation(summary = "我的考试记录（取最高分）")
    @GetMapping("/myRecords")
    public CommonResult<List<ExamRecordEntity>> myRecords(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return CommonResult.success(examRecordService.listMyExamRecords(loginUser.getUserId()));
    }
}
