package com.smart.nursing.controller.mobile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smart.nursing.aop.LoginUser;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.exception.BusinessException;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.common.utils.SecurityUtils;
import com.smart.nursing.dto.ExamRecordDto;
import com.smart.nursing.dto.LearningRecordDto;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.*;
import com.smart.nursing.vo.LearningProgressVo;
import com.smart.nursing.vo.LearningRecordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 移动端用户 Controller
 */
@Tag(name = "移动端-用户", description = "个人信息、密码修改、学习进度、学习记录、考试记录")
@RestController
@RequestMapping("/mobile/user")
@RequiredArgsConstructor
public class MobileUserController {

    private final IUserService userService;
    private final ILearningService learningService;
    private final IExamRecordService examRecordService;
    private final IArticleService articleService;
    private final IVideoService videoService;
    private final IPptService pptService;

    @Operation(summary = "当前用户信息")
    @GetMapping("/info")
    public CommonResult<UserEntity> info(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        UserEntity user = userService.getById(loginUser.getUserId());
        if (user != null) {
            user.setPassword(null);
        }
        return CommonResult.success(user);
    }

    @Operation(summary = "修改个人信息")
    @PostMapping("/updateProfile")
    public CommonResult<Void> updateProfile(HttpServletRequest request,
                                            @RequestBody UserEntity entity) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        UserEntity user = userService.getById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(GlobalErrorCodeConstants.USER_NOT_EXIST);
        }
        user.setRealName(entity.getRealName());
        user.setPhone(entity.getPhone());
        user.setEmail(entity.getEmail());
        user.setAvatar(entity.getAvatar());
        userService.updateById(user);
        return CommonResult.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    public CommonResult<Void> changePassword(HttpServletRequest request,
                                             @RequestParam String oldPassword,
                                             @RequestParam String newPassword) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        UserEntity user = userService.getById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(GlobalErrorCodeConstants.USER_NOT_EXIST);
        }
        if (!SecurityUtils.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(GlobalErrorCodeConstants.PASSWORD_ERROR);
        }
        user.setPassword(SecurityUtils.encode(newPassword));
        userService.updateById(user);
        return CommonResult.success();
    }

    @Operation(summary = "学习进度统计")
    @GetMapping("/progress")
    public CommonResult<LearningProgressVo> progress(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return CommonResult.success(learningService.getLearningProgress(loginUser.getUserId()));
    }

    @Operation(summary = "学习记录")
    @GetMapping("/records")
    public CommonResult<?> records(HttpServletRequest request,
                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        LearningRecordDto dto = new LearningRecordDto();
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        dto.setUserId(loginUser.getUserId());
        IPage<LearningRecordVo> page = learningService.listLearningRecords(dto);

        // 转换为带标题和类型名的列表（VO已关联查询内容标题）
        List<Map<String, Object>> enrichedRecords = new ArrayList<>();
        for (LearningRecordVo record : page.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("recordId", record.getRecordId());
            item.put("userId", record.getUserId());
            item.put("contentType", record.getContentType());
            item.put("contentId", record.getContentId());
            item.put("title", record.getContentTitle());
            item.put("typeName", getTypeName(record.getContentType()));
            item.put("progress", record.getProgress());
            item.put("studyDuration", record.getStudyDuration());
            item.put("lastStudyTime", record.getLastStudyTime());
            enrichedRecords.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", enrichedRecords);
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        return CommonResult.success(result);
    }

    @Operation(summary = "考试记录")
    @GetMapping("/exams")
    public CommonResult<?> exams(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        ExamRecordDto dto = new ExamRecordDto();
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        dto.setUserId(loginUser.getUserId());
        return CommonResult.successPageData(examRecordService.listRecordByCondition(dto));
    }

    /**
     * 根据内容类型和ID查询标题
     */
    private String getContentTitle(Integer contentType, Long contentId) {
        try {
            switch (contentType) {
                case 1:
                    ArticleEntity article = articleService.getById(contentId);
                    return article != null ? article.getTitle() : "内容已删除";
                case 2:
                    VideoEntity video = videoService.getById(contentId);
                    return video != null ? video.getTitle() : "内容已删除";
                case 3:
                    PptEntity ppt = pptService.getById(contentId);
                    return ppt != null ? ppt.getTitle() : "内容已删除";
                default:
                    return "未知内容";
            }
        } catch (Exception e) {
            return "内容已删除";
        }
    }

    /**
     * 获取类型名称
     */
    private String getTypeName(Integer contentType) {
        switch (contentType) {
            case 1: return "文章";
            case 2: return "视频";
            case 3: return "课件";
            default: return "未知";
        }
    }
}
