package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.PptDto;
import com.smart.nursing.service.IPptService;
import com.smart.nursing.vo.PptVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 课件管理 Controller（管理端）
 */
@Tag(name = "课件管理", description = "护理课件（PPT）的增删改查")
@RestController
@RequestMapping("/admin/ppt")
@RequiredArgsConstructor
public class PptController {

    private final IPptService pptService;

    @Operation(summary = "分页查询课件")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody PptDto dto) {
        return CommonResult.successPageData(pptService.listPptByCondition(dto));
    }

    @Operation(summary = "根据ID获取课件详情")
    @GetMapping("/getById/{pptId}")
    public CommonResult<PptVo> getById(@PathVariable Long pptId) {
        return CommonResult.success(pptService.getPptById(pptId));
    }

    @RecordLog("新增课件")
    @Operation(summary = "新增课件")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody PptDto dto) {
        pptService.addPpt(dto);
        return CommonResult.success();
    }

    @RecordLog("修改课件")
    @Operation(summary = "修改课件")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody PptDto dto) {
        pptService.updatePpt(dto);
        return CommonResult.success();
    }

    @RecordLog("删除课件")
    @Operation(summary = "删除课件")
    @PostMapping("/delete/{pptId}")
    public CommonResult<Void> delete(@PathVariable Long pptId) {
        pptService.deletePpt(pptId);
        return CommonResult.success();
    }
}
