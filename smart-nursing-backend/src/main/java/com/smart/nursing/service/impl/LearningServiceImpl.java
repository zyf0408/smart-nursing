package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.ArticleMapper;
import com.smart.nursing.dao.LearningRecordMapper;
import com.smart.nursing.dao.PptMapper;
import com.smart.nursing.dao.UserMapper;
import com.smart.nursing.dao.VideoMapper;
import com.smart.nursing.dto.LearningRecordDto;
import com.smart.nursing.entity.ArticleEntity;
import com.smart.nursing.entity.LearningRecordEntity;
import com.smart.nursing.entity.PptEntity;
import com.smart.nursing.entity.UserEntity;
import com.smart.nursing.entity.VideoEntity;
import com.smart.nursing.service.ILearningService;
import com.smart.nursing.vo.LearningProgressVo;
import com.smart.nursing.vo.LearningRecordVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecordEntity> implements ILearningService {

    private static final int MAX_RETRY = 3;

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final VideoMapper videoMapper;
    private final PptMapper pptMapper;

    @Override
    public void updateProgress(Long userId, Integer contentType, Long contentId, Integer progress, Integer studyDuration) {
        // 1. 查找或创建学习记录
        LearningRecordEntity record = this.getOne(new LambdaQueryWrapper<LearningRecordEntity>()
                .eq(LearningRecordEntity::getUserId, userId)
                .eq(LearningRecordEntity::getContentType, contentType)
                .eq(LearningRecordEntity::getContentId, contentId));

        if (record == null) {
            // 创建新记录
            record = new LearningRecordEntity();
            record.setUserId(userId);
            record.setContentType(contentType);
            record.setContentId(contentId);
            record.setProgress(progress != null ? progress : 0);
            record.setStudyDuration(studyDuration != null ? studyDuration : 0);
            record.setLastStudyTime(LocalDateTime.now());
            record.setIsCompleted(progress != null && progress >= 100 ? 1 : 0);
            record.setVersion(0);
            this.save(record);
            return;
        }

        // 2. 乐观锁更新（version+1），重试机制
        int newProgress = Math.max(record.getProgress() != null ? record.getProgress() : 0, progress != null ? progress : 0);
        int newDuration = (record.getStudyDuration() != null ? record.getStudyDuration() : 0) + (studyDuration != null ? studyDuration : 0);
        record.setProgress(newProgress);
        record.setStudyDuration(newDuration);
        record.setLastStudyTime(LocalDateTime.now());
        // 进度100则标记完成
        if (newProgress >= 100) {
            record.setIsCompleted(1);
        }

        for (int i = 0; i < MAX_RETRY; i++) {
            boolean success = this.updateById(record);
            if (success) {
                return;
            }
            // 版本冲突，重新查询后重试
            log.warn("乐观锁更新失败，第 {} 次重试", i + 1);
            record = this.getById(record.getRecordId());
            if (record == null) {
                return;
            }
            newProgress = Math.max(record.getProgress() != null ? record.getProgress() : 0, progress != null ? progress : 0);
            newDuration = (record.getStudyDuration() != null ? record.getStudyDuration() : 0) + (studyDuration != null ? studyDuration : 0);
            record.setProgress(newProgress);
            record.setStudyDuration(newDuration);
            record.setLastStudyTime(LocalDateTime.now());
            if (newProgress >= 100) {
                record.setIsCompleted(1);
            }
        }
        log.error("乐观锁更新学习记录失败，超过最大重试次数");
    }

    @Override
    public LearningProgressVo getLearningProgress(Long userId) {
        LearningProgressVo vo = new LearningProgressVo();
        vo.setUserId(userId);
        // 查用户名
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        // 查用户全部学习记录
        List<LearningRecordEntity> allRecords = this.list(new LambdaQueryWrapper<LearningRecordEntity>()
                .eq(LearningRecordEntity::getUserId, userId));
        // 统计内容总数（文章+视频+课件）
        long articleCount = articleMapper.selectCount(null);
        long videoCount = videoMapper.selectCount(null);
        long pptCount = pptMapper.selectCount(null);
        long totalContent = articleCount + videoCount + pptCount;
        vo.setTotalContent(totalContent);
        // 已完成内容数
        long completedContent = allRecords.stream()
                .filter(r -> r.getIsCompleted() != null && r.getIsCompleted() == 1)
                .count();
        vo.setCompletedContent(completedContent);
        // 总学习时长
        long totalDuration = allRecords.stream()
                .mapToLong(r -> r.getStudyDuration() != null ? r.getStudyDuration() : 0)
                .sum();
        vo.setTotalDuration(totalDuration);
        // 学习进度百分比
        if (totalContent > 0) {
            vo.setProgressPercent(Math.round(completedCount(allRecords) * 10000.0 / totalContent) / 100.0);
        } else {
            vo.setProgressPercent(0.0);
        }
        // 最近学习记录
        List<LearningRecordEntity> recentRecords = this.list(new LambdaQueryWrapper<LearningRecordEntity>()
                .eq(LearningRecordEntity::getUserId, userId)
                .orderByDesc(LearningRecordEntity::getLastStudyTime)
                .last("LIMIT 10"));
        vo.setRecentRecords(recentRecords);
        return vo;
    }

    @Override
    public IPage<LearningRecordVo> listLearningRecords(LearningRecordDto dto) {
        Page<LearningRecordVo> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return baseMapper.selectRecordPage(page, dto);
    }

    /**
     * 计算已完成的学习记录数
     */
    private long completedCount(List<LearningRecordEntity> records) {
        return records.stream()
                .filter(r -> r.getIsCompleted() != null && r.getIsCompleted() == 1)
                .count();
    }
}
