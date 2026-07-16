package com.smart.nursing.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.LogEntity;
import com.smart.nursing.service.ILogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志 Controller（管理端）
 */
@Tag(name = "日志管理", description = "操作日志查询")
@RestController
@RequestMapping("/admin/log")
@RequiredArgsConstructor
public class LogController {

    private final ILogService logService;

    @Operation(summary = "分页查询日志")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestParam(defaultValue = "1") Integer pageNo,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) Integer logType) {
        Page<LogEntity> page = new Page<>(pageNo, pageSize);
        return CommonResult.successPageData(logService.listLogByCondition(page, username, logType));
    }
}
