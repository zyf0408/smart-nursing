package com.smart.nursing.controller.admin;

import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.service.IDataCountService;
import com.smart.nursing.vo.DashboardVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计 Controller（管理端）
 */
@Tag(name = "数据统计", description = "首页仪表盘统计数据")
@RestController
@RequestMapping("/admin/dataCount")
@RequiredArgsConstructor
public class DataCountController {

    private final IDataCountService dataCountService;

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/dashboard")
    public CommonResult<DashboardVo> dashboard() {
        return CommonResult.success(dataCountService.getDashboardData());
    }
}
