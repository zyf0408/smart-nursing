package com.smart.nursing.controller.admin;

import com.smart.nursing.aop.RecordLog;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.dto.VideoDto;
import com.smart.nursing.service.IVideoService;
import com.smart.nursing.vo.VideoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 视频管理 Controller（管理端）
 */
@Tag(name = "视频管理", description = "护理视频的增删改查")
@RestController
@RequestMapping("/admin/video")
@RequiredArgsConstructor
public class VideoController {

    private final IVideoService videoService;

    @Operation(summary = "分页查询视频")
    @PostMapping("/list")
    public CommonResult<?> list(@RequestBody VideoDto dto) {
        return CommonResult.successPageData(videoService.listVideoByCondition(dto));
    }

    @Operation(summary = "根据ID获取视频详情")
    @GetMapping("/getById/{videoId}")
    public CommonResult<VideoVo> getById(@PathVariable Long videoId) {
        return CommonResult.success(videoService.getVideoById(videoId));
    }

    @RecordLog("新增视频")
    @Operation(summary = "新增视频")
    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody VideoDto dto) {
        videoService.addVideo(dto);
        return CommonResult.success();
    }

    @RecordLog("修改视频")
    @Operation(summary = "修改视频")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody VideoDto dto) {
        videoService.updateVideo(dto);
        return CommonResult.success();
    }

    @RecordLog("删除视频")
    @Operation(summary = "删除视频")
    @PostMapping("/delete/{videoId}")
    public CommonResult<Void> delete(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return CommonResult.success();
    }
}
