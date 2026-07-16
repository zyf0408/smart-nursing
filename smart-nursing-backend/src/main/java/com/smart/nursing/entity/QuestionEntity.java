package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 试题实体
 */
@Data
@ToString
@TableName("nursing_question")
public class QuestionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 试题ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 题型（1-单选 2-多选 3-判断）
     */
    private Integer questionType;

    /**
     * 题干内容
     */
    private String content;

    /**
     * 选项A
     */
    private String optionA;

    /**
     * 选项B
     */
    private String optionB;

    /**
     * 选项C
     */
    private String optionC;

    /**
     * 选项D
     */
    private String optionD;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标识（0-未删除 1-已删除）
     */
    @TableLogic
    private Integer isDelete;
}
