package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.ExamDto;
import com.smart.nursing.entity.ExamEntity;
import com.smart.nursing.vo.ExamDetailVo;

import java.util.List;

/**
 * 考试 Service 接口
 */
public interface IExamService extends IService<ExamEntity> {

    /**
     * 分页条件查询考试
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<ExamEntity> listExamByCondition(ExamDto dto);

    /**
     * 获取考试详情（含试题列表）
     *
     * @param examId 考试ID
     * @return 考试详情 VO
     */
    ExamDetailVo getExamDetail(Long examId);

    /**
     * 新增考试
     *
     * @param entity 考试信息
     */
    void addExam(ExamEntity entity);

    /**
     * 修改考试
     *
     * @param entity 考试信息
     */
    void updateExam(ExamEntity entity);

    /**
     * 删除考试
     *
     * @param examId 考试ID
     */
    void deleteExam(Long examId);

    /**
     * 发布考试
     *
     * @param examId 考试ID
     */
    void publishExam(Long examId);

    /**
     * 考试关联试题（组卷）
     *
     * @param examId      考试ID
     * @param questionIds 试题ID列表
     */
    void assignQuestions(Long examId, List<Long> questionIds);
}
