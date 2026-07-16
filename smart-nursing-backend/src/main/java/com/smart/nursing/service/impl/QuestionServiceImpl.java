package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.QuestionMapper;
import com.smart.nursing.dto.QuestionDto;
import com.smart.nursing.entity.QuestionEntity;
import com.smart.nursing.service.IQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 试题 Service 实现类
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionEntity> implements IQuestionService {

    @Override
    public IPage<QuestionEntity> listQuestionByCondition(QuestionDto dto) {
        Page<QuestionEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        LambdaQueryWrapper<QuestionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getCategoryId() != null, QuestionEntity::getCategoryId, dto.getCategoryId());
        wrapper.eq(dto.getQuestionType() != null, QuestionEntity::getQuestionType, dto.getQuestionType());
        wrapper.like(StringUtils.hasText(dto.getContent()), QuestionEntity::getContent, dto.getContent());
        wrapper.orderByDesc(QuestionEntity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void addQuestion(QuestionEntity entity) {
        this.save(entity);
    }

    @Override
    public void updateQuestion(QuestionEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        this.removeById(questionId);
    }
}
