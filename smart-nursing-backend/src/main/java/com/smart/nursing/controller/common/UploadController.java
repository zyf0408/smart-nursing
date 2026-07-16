package com.smart.nursing.controller.common;

import com.smart.nursing.common.exception.BusinessException;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.result.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传 Controller
 */
@Slf4j
@Tag(name = "通用接口", description = "文件上传等通用接口")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class UploadController {

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public CommonResult<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        // 获取原始文件名与扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成新文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
        // 保存到项目根目录的 upload/ 文件夹
        String uploadDir = "upload";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File destFile = new File(dir, newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        // 返回访问 URL
        String url = "/upload/" + newFileName;
        return CommonResult.success(url);
    }
}
