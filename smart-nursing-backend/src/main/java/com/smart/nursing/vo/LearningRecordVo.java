package com.smart.nursing.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习记录 VO（含用户名、内容标题）
 */
@Data
public class LearningRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 内容类型（1-文章 2-视频 3-课件）
     */
    private Integer contentType;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 学习内容标题
     */
    private String contentTitle;

    /**
     * 学习进度（0-100）
     */
    private Integer progress;

    /**
     * 学习时长（秒）
     */
    private Integer studyDuration;

    /**
     * 学习时长（分钟）
     */
    private Integer duration;

    /**
     * 最近学习时间
     */
    private LocalDateTime lastStudyTime;

    /**
     * 学习时间（对应前端 createTime 列）
     */
    private LocalDateTime createTime;

    /**
     * 是否完成（0-未完成 1-已完成）
     */
    private Integer isCompleted;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}
