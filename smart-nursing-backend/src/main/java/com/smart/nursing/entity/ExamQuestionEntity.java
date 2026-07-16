package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 考试-试题关联实体
 */
@Data
@ToString
@TableName("nursing_exam_question")
public class ExamQuestionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 试题ID
     */
    private Long questionId;

    /**
     * 排序号
     */
    private Integer sortOrder;
}
