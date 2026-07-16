package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.QuestionDto;
import com.smart.nursing.entity.QuestionEntity;

/**
 * 试题 Service 接口
 */
public interface IQuestionService extends IService<QuestionEntity> {

    /**
     * 分页条件查询试题
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<QuestionEntity> listQuestionByCondition(QuestionDto dto);

    /**
     * 新增试题
     *
     * @param entity 试题信息
     */
    void addQuestion(QuestionEntity entity);

    /**
     * 修改试题
     *
     * @param entity 试题信息
     */
    void updateQuestion(QuestionEntity entity);

    /**
     * 删除试题
     *
     * @param questionId 试题ID
     */
    void deleteQuestion(Long questionId);
}
