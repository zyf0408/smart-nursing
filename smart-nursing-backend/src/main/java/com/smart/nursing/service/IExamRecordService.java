package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.ExamRecordDto;
import com.smart.nursing.entity.ExamRecordEntity;
import com.smart.nursing.vo.ExamResultVo;

import java.util.List;

/**
 * 考试记录 Service 接口
 */
public interface IExamRecordService extends IService<ExamRecordEntity> {

    /**
     * 分页条件查询考试记录
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<ExamRecordEntity> listRecordByCondition(ExamRecordDto dto);

    /**
     * 提交考试（自动判分）
     *
     * @param examId  考试ID
     * @param userId  用户ID
     * @param answers 答题内容（JSON）
     * @return 考试结果 VO
     */
    ExamResultVo submitExam(Long examId, Long userId, String answers);

    /**
     * 获取考试结果
     *
     * @param recordId 记录ID
     * @return 考试结果 VO
     */
    ExamResultVo getExamResult(Long recordId);

    /**
     * 开始考试（创建考试记录）
     *
     * @param examId 考试ID
     * @param userId 用户ID
     */
    void startExam(Long examId, Long userId);

    /**
     * 查询学员的考试记录（同一考试取最高分作为最终成绩）
     *
     * @param userId 用户ID
     * @return 考试记录列表（含最高分）
     */
    List<ExamRecordEntity> listMyExamRecords(Long userId);
}
