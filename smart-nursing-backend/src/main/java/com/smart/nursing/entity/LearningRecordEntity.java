package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习记录实体
 */
@Data
@ToString
@TableName("nursing_learning_record")
public class LearningRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容类型（1-文章 2-视频 3-课件）
     */
    private Integer contentType;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 学习进度（0-100）
     */
    private Integer progress;

    /**
     * 学习时长（秒）
     */
    private Integer studyDuration;

    /**
     * 最近学习时间
     */
    private LocalDateTime lastStudyTime;

    /**
     * 是否完成（0-未完成 1-已完成）
     */
    private Integer isCompleted;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;
}
