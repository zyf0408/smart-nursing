package com.smart.nursing.controller.admin;

import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.LearningRecordDto;
import com.smart.nursing.service.ILearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学习记录 Controller（管理端）
 */
@Tag(name = "学习记录管理", description = "学习记录查询")
@RestController
@RequestMapping("/admin/learning")
@RequiredArgsConstructor
public class LearningController {

    private final ILearningService learningService;

    @Operation(summary = "分页查询学习记录")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody LearningRecordDto dto) {
        return CommonResult.successPageData(learningService.listLearningRecords(dto));
    }
}
