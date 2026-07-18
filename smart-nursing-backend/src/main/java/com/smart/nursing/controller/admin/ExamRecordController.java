package com.smart.nursing.controller.admin;

import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.ExamRecordDto;
import com.smart.nursing.service.IExamRecordService;
import com.smart.nursing.vo.ExamResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

    @Operation(summary = "导出成绩（CSV）")
    @PostMapping("/export")
    public void export(@RequestBody ExamRecordDto dto, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=exam_records.csv");
        // 查询全部记录（不分页）
        dto.setPageNo(1);
        dto.setPageSize(10000);
        var page = examRecordService.listRecordByCondition(dto);
        List<?> records = page.getRecords();
        // 写入 CSV
        PrintWriter writer = response.getWriter();
        writer.write("\uFEFF"); // BOM 头，确保 Excel 正确识别 UTF-8
        writer.write("考生姓名,考试名称,得分,总分,及格分,是否及格,用时(分),提交时间\n");
        for (Object obj : records) {
            var record = (com.smart.nursing.entity.ExamRecordEntity) obj;
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(record.getUsername()),
                    escapeCsv(record.getExamName()),
                    record.getScore() != null ? record.getScore() : "",
                    record.getTotalScore() != null ? record.getTotalScore() : "",
                    record.getPassScore() != null ? record.getPassScore() : "",
                    record.getIsPass() != null && record.getIsPass() == 1 ? "是" : "否",
                    record.getDuration() != null ? record.getDuration() : "",
                    record.getSubmitTime() != null ? record.getSubmitTime() : ""
            ));
        }
        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
