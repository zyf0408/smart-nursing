package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.ExamMapper;
import com.smart.nursing.dao.ExamQuestionMapper;
import com.smart.nursing.dao.QuestionMapper;
import com.smart.nursing.dto.ExamDto;
import com.smart.nursing.entity.ExamEntity;
import com.smart.nursing.entity.ExamQuestionEntity;
import com.smart.nursing.entity.QuestionEntity;
import com.smart.nursing.service.IExamService;
import com.smart.nursing.vo.ExamDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考试 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamMapper, ExamEntity> implements IExamService {

    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;

    @Override
    public IPage<ExamEntity> listExamByCondition(ExamDto dto) {
        Page<ExamEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        LambdaQueryWrapper<ExamEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getExamName()), ExamEntity::getExamName, dto.getExamName());
        wrapper.eq(dto.getStatus() != null, ExamEntity::getStatus, dto.getStatus());
        wrapper.orderByDesc(ExamEntity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public ExamDetailVo getExamDetail(Long examId) {
        ExamEntity exam = this.getById(examId);
        if (exam == null) {
            return null;
        }
        ExamDetailVo vo = new ExamDetailVo();
        BeanUtils.copyProperties(exam, vo);
        // 查询关联试题
        List<ExamQuestionEntity> examQuestions = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestionEntity>()
                .eq(ExamQuestionEntity::getExamId, examId)
                .orderByAsc(ExamQuestionEntity::getSortOrder));
        if (!examQuestions.isEmpty()) {
            List<Long> questionIds = examQuestions.stream()
                    .map(ExamQuestionEntity::getQuestionId)
                    .collect(Collectors.toList());
            List<QuestionEntity> questionList = questionMapper.selectBatchIds(questionIds);
            // 防作弊：打乱试题顺序（每个学员看到的题目顺序不同）
            Collections.shuffle(questionList);
            vo.setQuestionList(questionList);
        } else {
            vo.setQuestionList(new ArrayList<>());
        }
        return vo;
    }

    @Override
    public void addExam(ExamEntity entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        this.save(entity);
    }

    @Override
    public void updateExam(ExamEntity entity) {
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExam(Long examId) {
        this.removeById(examId);
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestionEntity>()
                .eq(ExamQuestionEntity::getExamId, examId));
    }

    @Override
    public void publishExam(Long examId) {
        ExamEntity exam = this.getById(examId);
        if (exam != null) {
            // 切换发布状态：已发布(1)→已结束(2)，未发布(0)→已发布(1)
            Integer currentStatus = exam.getStatus();
            exam.setStatus(currentStatus != null && currentStatus == 1 ? 2 : 1);
            this.updateById(exam);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignQuestions(Long examId, List<Long> questionIds) {
        // 先删除旧的关联
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestionEntity>()
                .eq(ExamQuestionEntity::getExamId, examId));
        // 再批量插入新的关联
        if (questionIds != null && !questionIds.isEmpty()) {
            int sortOrder = 1;
            for (Long questionId : questionIds) {
                ExamQuestionEntity eq = new ExamQuestionEntity();
                eq.setExamId(examId);
                eq.setQuestionId(questionId);
                eq.setSortOrder(sortOrder++);
                examQuestionMapper.insert(eq);
            }
        }
    }
}
