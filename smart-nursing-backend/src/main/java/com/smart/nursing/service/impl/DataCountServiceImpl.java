package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.nursing.dao.*;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.IDataCountService;
import com.smart.nursing.vo.DashboardVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class DataCountServiceImpl implements IDataCountService {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final VideoMapper videoMapper;
    private final PptMapper pptMapper;
    private final ExamMapper examMapper;
    private final CategoryMapper categoryMapper;
    private final ExamRecordMapper examRecordMapper;
    private final LearningRecordMapper learningRecordMapper;

    @Override
    public DashboardVo getDashboardData() {
        DashboardVo vo = new DashboardVo();
        // 1. 统计用户数
        vo.setUserCount(userMapper.selectCount(null));
        // 2. 统计文章数
        vo.setArticleCount(articleMapper.selectCount(null));
        // 3. 统计视频数
        vo.setVideoCount(videoMapper.selectCount(null));
        // 4. 统计考试数
        vo.setExamCount(examMapper.selectCount(null));
        // 5. 类别内容分布
        vo.setCategoryContentCounts(getCategoryContentCounts());
        // 6. 学习趋势（近7天）
        vo.setLearningTrend(getLearningTrend());
        // 7. 考试及格率
        vo.setExamPassRate(getExamPassRate());
        return vo;
    }

    /**
     * 获取各分类内容数量统计
     */
    private List<Map<String, Object>> getCategoryContentCounts() {
        List<CategoryEntity> categories = categoryMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CategoryEntity category : categories) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", category.getCategoryId());
            item.put("categoryName", category.getCategoryName());
            long articleCount = articleMapper.selectCount(new LambdaQueryWrapper<ArticleEntity>()
                    .eq(ArticleEntity::getCategoryId, category.getCategoryId()));
            long videoCount = videoMapper.selectCount(new LambdaQueryWrapper<VideoEntity>()
                    .eq(VideoEntity::getCategoryId, category.getCategoryId()));
            long pptCount = pptMapper.selectCount(new LambdaQueryWrapper<PptEntity>()
                    .eq(PptEntity::getCategoryId, category.getCategoryId()));
            item.put("articleCount", articleCount);
            item.put("videoCount", videoCount);
            item.put("pptCount", pptCount);
            item.put("totalCount", articleCount + videoCount + pptCount);
            result.add(item);
        }
        return result;
    }

    /**
     * 获取学习趋势（近7天）
     */
    private List<Map<String, Object>> getLearningTrend() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<LearningRecordEntity> records = learningRecordMapper.selectList(new LambdaQueryWrapper<LearningRecordEntity>()
                .ge(LearningRecordEntity::getLastStudyTime, sevenDaysAgo));
        // 按日期分组统计
        Map<String, Long> dailyCount = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getLastStudyTime().toLocalDate().toString(),
                        Collectors.counting()));
        // 补全7天数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", dailyCount.getOrDefault(date.toString(), 0L));
            result.add(item);
        }
        return result;
    }

    /**
     * 获取考试及格率
     */
    private Double getExamPassRate() {
        long totalCount = examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecordEntity>()
                .eq(ExamRecordEntity::getStatus, 2));
        if (totalCount == 0) {
            return 0.0;
        }
        long passCount = examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecordEntity>()
                .eq(ExamRecordEntity::getStatus, 2)
                .eq(ExamRecordEntity::getIsPass, 1));
        return Math.round(passCount * 10000.0 / totalCount) / 100.0;
    }
}
