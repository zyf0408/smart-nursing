package com.smart.nursing.controller.mobile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.nursing.aop.LoginUser;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.ArticleEntity;
import com.smart.nursing.entity.FavoriteEntity;
import com.smart.nursing.entity.LearningRecordEntity;
import com.smart.nursing.entity.PptEntity;
import com.smart.nursing.entity.VideoEntity;
import com.smart.nursing.service.IArticleService;
import com.smart.nursing.service.IFavoriteService;
import com.smart.nursing.service.ILearningService;
import com.smart.nursing.service.IPptService;
import com.smart.nursing.service.IVideoService;
import com.smart.nursing.vo.ArticleVo;
import com.smart.nursing.vo.PptVo;
import com.smart.nursing.vo.VideoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端学习 Controller
 */
@Tag(name = "移动端-学习", description = "学习内容浏览、搜索、进度上报、收藏")
@RestController
@RequestMapping("/mobile/learn")
@RequiredArgsConstructor
public class MobileLearnController {

    private final IArticleService articleService;
    private final IVideoService videoService;
    private final IPptService pptService;
    private final ILearningService learningService;
    private final IFavoriteService favoriteService;

    @Operation(summary = "学习内容列表（支持分类+类型+关键词筛选）")
    @GetMapping("/list")
    public CommonResult<List<Map<String, Object>>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer contentType,
            @RequestParam(required = false) String keyword) {

        List<Map<String, Object>> result = new ArrayList<>();

        // 1-文章 2-视频 3-课件；contentType 为 null 则查全部
        if (contentType == null || contentType == 1) {
            LambdaQueryWrapper<ArticleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(categoryId != null, ArticleEntity::getCategoryId, categoryId);
            wrapper.like(StringUtils.hasText(keyword), ArticleEntity::getTitle, keyword);
            wrapper.eq(ArticleEntity::getStatus, 1);
            wrapper.orderByDesc(ArticleEntity::getCreateTime);
            List<ArticleEntity> articles = articleService.list(wrapper);
            for (ArticleEntity a : articles) {
                Map<String, Object> item = new HashMap<>();
                item.put("contentType", 1);
                item.put("contentId", a.getArticleId());
                item.put("title", a.getTitle());
                item.put("coverImage", a.getCoverImage());
                item.put("summary", a.getSummary());
                item.put("viewCount", a.getViewCount());
                item.put("createTime", a.getCreateTime());
                result.add(item);
            }
        }
        if (contentType == null || contentType == 2) {
            LambdaQueryWrapper<VideoEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(categoryId != null, VideoEntity::getCategoryId, categoryId);
            wrapper.like(StringUtils.hasText(keyword), VideoEntity::getTitle, keyword);
            wrapper.eq(VideoEntity::getStatus, 1);
            wrapper.orderByDesc(VideoEntity::getCreateTime);
            List<VideoEntity> videos = videoService.list(wrapper);
            for (VideoEntity v : videos) {
                Map<String, Object> item = new HashMap<>();
                item.put("contentType", 2);
                item.put("contentId", v.getVideoId());
                item.put("title", v.getTitle());
                item.put("coverImage", v.getCoverImage());
                item.put("summary", v.getDescription());
                item.put("viewCount", v.getViewCount());
                item.put("duration", v.getDuration());
                item.put("createTime", v.getCreateTime());
                result.add(item);
            }
        }
        if (contentType == null || contentType == 3) {
            LambdaQueryWrapper<PptEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(categoryId != null, PptEntity::getCategoryId, categoryId);
            wrapper.like(StringUtils.hasText(keyword), PptEntity::getTitle, keyword);
            wrapper.eq(PptEntity::getStatus, 1);
            wrapper.orderByDesc(PptEntity::getCreateTime);
            List<PptEntity> ppts = pptService.list(wrapper);
            for (PptEntity p : ppts) {
                Map<String, Object> item = new HashMap<>();
                item.put("contentType", 3);
                item.put("contentId", p.getPptId());
                item.put("title", p.getTitle());
                item.put("coverImage", p.getCoverImage());
                item.put("summary", p.getDescription());
                item.put("viewCount", p.getViewCount());
                item.put("createTime", p.getCreateTime());
                result.add(item);
            }
        }
        return CommonResult.success(result);
    }

    @Operation(summary = "内容详情")
    @GetMapping("/detail/{contentType}/{contentId}")
    public CommonResult<Object> detail(@PathVariable Integer contentType,
                                       @PathVariable Long contentId,
                                       HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        // 查询当前用户是否已收藏此内容
        boolean isFavorited = favoriteService.isFavorite(loginUser.getUserId(), contentType, contentId);
        // 查询学习进度
        LearningRecordEntity record = learningService.getOne(
                new LambdaQueryWrapper<LearningRecordEntity>()
                        .eq(LearningRecordEntity::getUserId, loginUser.getUserId())
                        .eq(LearningRecordEntity::getContentType, contentType)
                        .eq(LearningRecordEntity::getContentId, contentId)
                        .orderByDesc(LearningRecordEntity::getLastStudyTime)
                        .last("LIMIT 1")
        );
        Map<String, Object> progressMap = null;
        if (record != null) {
            progressMap = new HashMap<>();
            progressMap.put("percent", record.getProgress());
            progressMap.put("duration", record.getStudyDuration());
        }

        switch (contentType) {
            case 1:
                ArticleVo articleVo = articleService.getArticleById(contentId);
                if (articleVo != null) {
                    articleVo.setIsFavorited(isFavorited);
                    if (progressMap != null) {
                        articleVo.setProgress(progressMap);
                    }
                }
                return CommonResult.success(articleVo);
            case 2:
                VideoVo videoVo = videoService.getVideoById(contentId);
                if (videoVo != null) {
                    videoVo.setIsFavorited(isFavorited);
                    if (progressMap != null) {
                        videoVo.setProgress(progressMap);
                    }
                }
                return CommonResult.success(videoVo);
            case 3:
                PptVo pptVo = pptService.getPptById(contentId);
                if (pptVo != null) {
                    pptVo.setIsFavorited(isFavorited);
                    if (progressMap != null) {
                        pptVo.setProgress(progressMap);
                    }
                }
                return CommonResult.success(pptVo);
            default:
                return CommonResult.success(null);
        }
    }

    @Operation(summary = "上报学习进度")
    @PostMapping("/progress")
    public CommonResult<Void> progress(HttpServletRequest request,
                                       @RequestParam Integer contentType,
                                       @RequestParam Long contentId,
                                       @RequestParam Integer progress,
                                       @RequestParam(required = false) Integer studyDuration) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        learningService.updateProgress(loginUser.getUserId(), contentType, contentId, progress, studyDuration);
        return CommonResult.success();
    }

    @Operation(summary = "搜索学习内容")
    @GetMapping("/search")
    public CommonResult<List<Map<String, Object>>> search(@RequestParam String keyword) {
        return CommonResult.success(searchContent(keyword));
    }

    @Operation(summary = "添加收藏")
    @PostMapping("/favorite/add")
    public CommonResult<Void> addFavorite(HttpServletRequest request,
                                          @RequestParam Integer contentType,
                                          @RequestParam Long contentId) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        favoriteService.addFavorite(loginUser.getUserId(), contentType, contentId);
        return CommonResult.success();
    }

    @Operation(summary = "取消收藏")
    @PostMapping("/favorite/remove")
    public CommonResult<Void> removeFavorite(HttpServletRequest request,
                                             @RequestParam Integer contentType,
                                             @RequestParam Long contentId) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        favoriteService.removeFavorite(loginUser.getUserId(), contentType, contentId);
        return CommonResult.success();
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/favorite/list")
    public CommonResult<List<Map<String, Object>>> favoriteList(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        List<FavoriteEntity> favorites = favoriteService.getFavoriteList(loginUser.getUserId());
        List<Map<String, Object>> result = new ArrayList<>();
        for (FavoriteEntity f : favorites) {
            Map<String, Object> item = new HashMap<>();
            item.put("favoriteId", f.getFavoriteId());
            item.put("contentType", f.getContentType());
            item.put("contentId", f.getContentId());
            item.put("favoriteTime", f.getCreateTime());
            // 关联查询内容标题
            String title = getContentTitle(f.getContentType(), f.getContentId());
            item.put("title", title);
            result.add(item);
        }
        return CommonResult.success(result);
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
     * 搜索内容（文章+视频+课件）
     */
    private List<Map<String, Object>> searchContent(String keyword) {
        List<Map<String, Object>> result = new ArrayList<>();
        // 文章
        List<ArticleEntity> articles = articleService.list(new LambdaQueryWrapper<ArticleEntity>()
                .like(ArticleEntity::getTitle, keyword)
                .eq(ArticleEntity::getStatus, 1));
        for (ArticleEntity a : articles) {
            Map<String, Object> item = new HashMap<>();
            item.put("contentType", 1);
            item.put("contentId", a.getArticleId());
            item.put("title", a.getTitle());
            item.put("coverImage", a.getCoverImage());
            result.add(item);
        }
        // 视频
        List<VideoEntity> videos = videoService.list(new LambdaQueryWrapper<VideoEntity>()
                .like(VideoEntity::getTitle, keyword)
                .eq(VideoEntity::getStatus, 1));
        for (VideoEntity v : videos) {
            Map<String, Object> item = new HashMap<>();
            item.put("contentType", 2);
            item.put("contentId", v.getVideoId());
            item.put("title", v.getTitle());
            item.put("coverImage", v.getCoverImage());
            result.add(item);
        }
        // 课件
        List<PptEntity> ppts = pptService.list(new LambdaQueryWrapper<PptEntity>()
                .like(PptEntity::getTitle, keyword)
                .eq(PptEntity::getStatus, 1));
        for (PptEntity p : ppts) {
            Map<String, Object> item = new HashMap<>();
            item.put("contentType", 3);
            item.put("contentId", p.getPptId());
            item.put("title", p.getTitle());
            item.put("coverImage", p.getCoverImage());
            result.add(item);
        }
        return result;
    }
}
