package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.LearningRecordDto;
import com.smart.nursing.entity.LearningRecordEntity;
import com.smart.nursing.vo.LearningProgressVo;
import com.smart.nursing.vo.LearningRecordVo;

/**
 * 学习记录 Service 接口
 */
public interface ILearningService extends IService<LearningRecordEntity> {

    /**
     * 更新学习进度
     *
     * @param userId        用户ID
     * @param contentType   内容类型（1-文章 2-视频 3-课件）
     * @param contentId     内容ID
     * @param progress      学习进度（0-100）
     * @param studyDuration 学习时长（秒）
     */
    void updateProgress(Long userId, Integer contentType, Long contentId, Integer progress, Integer studyDuration);

    /**
     * 获取用户学习进度统计
     *
     * @param userId 用户ID
     * @return 学习进度 VO
     */
    LearningProgressVo getLearningProgress(Long userId);

    /**
     * 分页查询学习记录
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<LearningRecordVo> listLearningRecords(LearningRecordDto dto);
}
