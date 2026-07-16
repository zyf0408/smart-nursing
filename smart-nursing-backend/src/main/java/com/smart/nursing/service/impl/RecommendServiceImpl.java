package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.nursing.dao.*;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 个性化推荐 Service 实现类
 * <p>
 * 推荐策略：
 * 1. 查用户最近学习的内容类别
 * 2. 查用户考试薄弱环节（未及格考试涉及的试题分类）
 * 3. 从这些类别中推荐未学习的内容
 * 4. 如果数据不足，推荐热门内容（按浏览量降序）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    /** 内容类型常量 */
    private static final int CONTENT_TYPE_ARTICLE = 1;
    private static final int CONTENT_TYPE_VIDEO = 2;
    private static final int CONTENT_TYPE_PPT = 3;

    /** 推荐结果最大数量 */
    private static final int MAX_RECOMMEND_SIZE = 10;
    /** 触发热门推荐兜底的数量阈值 */
    private static final int MIN_RECOMMEND_THRESHOLD = 5;

    private final LearningRecordMapper learningRecordMapper;
    private final ArticleMapper articleMapper;
    private final VideoMapper videoMapper;
    private final PptMapper pptMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public List<Map<String, Object>> recommend(Long userId) {
        // 感兴趣的分类集合（保持插入顺序）
        Set<Long> interestedCategoryIds = new LinkedHashSet<>();
        // 已学习的内容ID集合（按类型区分，用于排除已学内容）
        Set<Long> learnedArticleIds = new HashSet<>();
        Set<Long> learnedVideoIds = new HashSet<>();
        Set<Long> learnedPptIds = new HashSet<>();

        // 1. 查用户最近学习的内容类别
        collectLearningInterests(userId, interestedCategoryIds, learnedArticleIds, learnedVideoIds, learnedPptIds);

        // 2. 查用户考试薄弱环节
        collectExamWeakCategories(userId, interestedCategoryIds);

        // 3. 从感兴趣类别中推荐未学习的内容
        List<Map<String, Object>> result = new ArrayList<>();
        if (!interestedCategoryIds.isEmpty()) {
            result.addAll(recommendArticles(interestedCategoryIds, learnedArticleIds));
            result.addAll(recommendVideos(interestedCategoryIds, learnedVideoIds));
            result.addAll(recommendPpts(interestedCategoryIds, learnedPptIds));
        }

        // 4. 如果数据不足，推荐热门内容
        if (result.size() < MIN_RECOMMEND_THRESHOLD) {
            result.addAll(recommendPopularArticles(learnedArticleIds));
            result.addAll(recommendPopularVideos(learnedVideoIds));
            result.addAll(recommendPopularPpts(learnedPptIds));
        }

        // 限制返回数量
        if (result.size() > MAX_RECOMMEND_SIZE) {
            result = new ArrayList<>(result.subList(0, MAX_RECOMMEND_SIZE));
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 收集用户学习记录中的兴趣分类，并记录已学内容ID
     */
    private void collectLearningInterests(Long userId, Set<Long> interestedCategoryIds,
                                          Set<Long> learnedArticleIds, Set<Long> learnedVideoIds,
                                          Set<Long> learnedPptIds) {
        List<LearningRecordEntity> records = learningRecordMapper.selectList(
                new LambdaQueryWrapper<LearningRecordEntity>()
                        .eq(LearningRecordEntity::getUserId, userId)
                        .orderByDesc(LearningRecordEntity::getLastStudyTime)
                        .last("LIMIT 20"));
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        // 按内容类型分组收集内容ID
        List<Long> articleIds = new ArrayList<>();
        List<Long> videoIds = new ArrayList<>();
        List<Long> pptIds = new ArrayList<>();
        for (LearningRecordEntity record : records) {
            Integer type = record.getContentType();
            Long contentId = record.getContentId();
            if (type == null || contentId == null) {
                continue;
            }
            switch (type) {
                case CONTENT_TYPE_ARTICLE:
                    articleIds.add(contentId);
                    learnedArticleIds.add(contentId);
                    break;
                case CONTENT_TYPE_VIDEO:
                    videoIds.add(contentId);
                    learnedVideoIds.add(contentId);
                    break;
                case CONTENT_TYPE_PPT:
                    pptIds.add(contentId);
                    learnedPptIds.add(contentId);
                    break;
                default:
                    break;
            }
        }

        // 批量查询内容所属分类
        addCategoryIds(articleMapper.selectBatchIds(articleIds), interestedCategoryIds, ArticleEntity::getCategoryId);
        addCategoryIds(videoMapper.selectBatchIds(videoIds), interestedCategoryIds, VideoEntity::getCategoryId);
        addCategoryIds(pptMapper.selectBatchIds(pptIds), interestedCategoryIds, PptEntity::getCategoryId);
    }

    /**
     * 收集用户考试薄弱环节涉及的分类
     */
    private void collectExamWeakCategories(Long userId, Set<Long> interestedCategoryIds) {
        // 查未及格的考试记录（按得分升序，取最近 5 次）
        List<ExamRecordEntity> examRecords = examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecordEntity>()
                        .eq(ExamRecordEntity::getUserId, userId)
                        .eq(ExamRecordEntity::getStatus, 2)
                        .eq(ExamRecordEntity::getIsPass, 0)
                        .orderByAsc(ExamRecordEntity::getScore)
                        .last("LIMIT 5"));
        if (CollectionUtils.isEmpty(examRecords)) {
            return;
        }

        // 收集这些考试涉及的所有试题ID
        Set<Long> questionIds = new HashSet<>();
        for (ExamRecordEntity record : examRecords) {
            List<ExamQuestionEntity> examQuestions = examQuestionMapper.selectList(
                    new LambdaQueryWrapper<ExamQuestionEntity>()
                            .eq(ExamQuestionEntity::getExamId, record.getExamId()));
            examQuestions.forEach(eq -> {
                if (eq.getQuestionId() != null) {
                    questionIds.add(eq.getQuestionId());
                }
            });
        }
        if (questionIds.isEmpty()) {
            return;
        }

        // 批量查询试题所属分类
        List<QuestionEntity> questions = questionMapper.selectBatchIds(questionIds);
        for (QuestionEntity question : questions) {
            if (question.getCategoryId() != null) {
                interestedCategoryIds.add(question.getCategoryId());
            }
        }
    }

    /**
     * 推荐感兴趣分类下的未学习文章（按浏览量降序）
     */
    private List<Map<String, Object>> recommendArticles(Set<Long> categoryIds, Set<Long> learnedIds) {
        List<ArticleEntity> articles = articleMapper.selectList(
                new LambdaQueryWrapper<ArticleEntity>()
                        .in(ArticleEntity::getCategoryId, categoryIds)
                        .eq(ArticleEntity::getStatus, 1)
                        .notIn(!learnedIds.isEmpty(), ArticleEntity::getArticleId, learnedIds)
                        .orderByDesc(ArticleEntity::getViewCount)
                        .last("LIMIT 5"));
        return articles.stream().map(a -> buildItemMap("article", a.getArticleId(),
                a.getTitle(), a.getCategoryId(), a.getViewCount())).collect(Collectors.toList());
    }

    /**
     * 推荐感兴趣分类下的未学习视频（按浏览量降序）
     */
    private List<Map<String, Object>> recommendVideos(Set<Long> categoryIds, Set<Long> learnedIds) {
        List<VideoEntity> videos = videoMapper.selectList(
                new LambdaQueryWrapper<VideoEntity>()
                        .in(VideoEntity::getCategoryId, categoryIds)
                        .eq(VideoEntity::getStatus, 1)
                        .notIn(!learnedIds.isEmpty(), VideoEntity::getVideoId, learnedIds)
                        .orderByDesc(VideoEntity::getViewCount)
                        .last("LIMIT 5"));
        return videos.stream().map(v -> buildItemMap("video", v.getVideoId(),
                v.getTitle(), v.getCategoryId(), v.getViewCount())).collect(Collectors.toList());
    }

    /**
     * 推荐感兴趣分类下的未学习课件（按浏览量降序）
     */
    private List<Map<String, Object>> recommendPpts(Set<Long> categoryIds, Set<Long> learnedIds) {
        List<PptEntity> ppts = pptMapper.selectList(
                new LambdaQueryWrapper<PptEntity>()
                        .in(PptEntity::getCategoryId, categoryIds)
                        .eq(PptEntity::getStatus, 1)
                        .notIn(!learnedIds.isEmpty(), PptEntity::getPptId, learnedIds)
                        .orderByDesc(PptEntity::getViewCount)
                        .last("LIMIT 5"));
        return ppts.stream().map(p -> buildItemMap("ppt", p.getPptId(),
                p.getTitle(), p.getCategoryId(), p.getViewCount())).collect(Collectors.toList());
    }

    /**
     * 推荐热门文章（按浏览量降序，排除已学）
     */
    private List<Map<String, Object>> recommendPopularArticles(Set<Long> learnedIds) {
        List<ArticleEntity> articles = articleMapper.selectList(
                new LambdaQueryWrapper<ArticleEntity>()
                        .eq(ArticleEntity::getStatus, 1)
                        .notIn(!learnedIds.isEmpty(), ArticleEntity::getArticleId, learnedIds)
                        .orderByDesc(ArticleEntity::getViewCount)
                        .last("LIMIT 5"));
        return articles.stream().map(a -> buildItemMap("article", a.getArticleId(),
                a.getTitle(), a.getCategoryId(), a.getViewCount())).collect(Collectors.toList());
    }

    /**
     * 推荐热门视频（按浏览量降序，排除已学）
     */
    private List<Map<String, Object>> recommendPopularVideos(Set<Long> learnedIds) {
        List<VideoEntity> videos = videoMapper.selectList(
                new LambdaQueryWrapper<VideoEntity>()
                        .eq(VideoEntity::getStatus, 1)
                        .notIn(!learnedIds.isEmpty(), VideoEntity::getVideoId, learnedIds)
                        .orderByDesc(VideoEntity::getViewCount)
                        .last("LIMIT 5"));
        return videos.stream().map(v -> buildItemMap("video", v.getVideoId(),
                v.getTitle(), v.getCategoryId(), v.getViewCount())).collect(Collectors.toList());
    }

    /**
     * 推荐热门课件（按浏览量降序，排除已学）
     */
    private List<Map<String, Object>> recommendPopularPpts(Set<Long> learnedIds) {
        List<PptEntity> ppts = pptMapper.selectList(
                new LambdaQueryWrapper<PptEntity>()
                        .eq(PptEntity::getStatus, 1)
                        .notIn(!learnedIds.isEmpty(), PptEntity::getPptId, learnedIds)
                        .orderByDesc(PptEntity::getViewCount)
                        .last("LIMIT 5"));
        return ppts.stream().map(p -> buildItemMap("ppt", p.getPptId(),
                p.getTitle(), p.getCategoryId(), p.getViewCount())).collect(Collectors.toList());
    }

    /**
     * 将分类ID集合补充到感兴趣集合中
     */
    private <T> void addCategoryIds(List<T> entities, Set<Long> interestedCategoryIds,
                                    java.util.function.Function<T, Long> categoryIdExtractor) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        for (T entity : entities) {
            Long categoryId = categoryIdExtractor.apply(entity);
            if (categoryId != null) {
                interestedCategoryIds.add(categoryId);
            }
        }
    }

    /**
     * 构建推荐项 Map
     */
    private Map<String, Object> buildItemMap(String contentType, Long contentId,
                                             String title, Long categoryId, Integer viewCount) {
        Map<String, Object> item = new HashMap<>();
        item.put("contentType", contentType);
        item.put("contentId", contentId);
        item.put("title", title);
        item.put("categoryId", categoryId);
        item.put("viewCount", viewCount);
        // 补充分类名称
        if (categoryId != null) {
            CategoryEntity category = categoryMapper.selectById(categoryId);
            if (category != null) {
                item.put("categoryName", category.getCategoryName());
            }
        }
        return item;
    }
}
