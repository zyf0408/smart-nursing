package com.smart.nursing.vo;

import com.smart.nursing.entity.LearningRecordEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学习进度统计 VO
 */
@Data
public class LearningProgressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 内容总数
     */
    private Long totalContent;

    /**
     * 已完成内容数
     */
    private Long completedContent;

    /**
     * 总学习时长（秒）
     */
    private Long totalDuration;

    /**
     * 学习进度百分比
     */
    private Double progressPercent;

    /**
     * 最近学习记录列表
     */
    private List<LearningRecordEntity> recentRecords;
}
