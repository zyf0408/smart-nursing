package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.StudyPlanMapper;
import com.smart.nursing.dto.StudyPlanDto;
import com.smart.nursing.entity.StudyPlanEntity;
import com.smart.nursing.service.IStudyPlanService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 学习计划 Service 实现类
 */
@Service
public class StudyPlanServiceImpl extends ServiceImpl<StudyPlanMapper, StudyPlanEntity> implements IStudyPlanService {

    @Override
    public IPage<StudyPlanEntity> listPlanByCondition(StudyPlanDto dto) {
        Page<StudyPlanEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        LambdaQueryWrapper<StudyPlanEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getUserId() != null, StudyPlanEntity::getUserId, dto.getUserId());
        wrapper.like(StringUtils.hasText(dto.getPlanName()), StudyPlanEntity::getPlanName, dto.getPlanName());
        wrapper.eq(dto.getStatus() != null, StudyPlanEntity::getStatus, dto.getStatus());
        wrapper.orderByDesc(StudyPlanEntity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void addPlan(StudyPlanEntity entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        this.save(entity);
    }

    @Override
    public void updatePlan(StudyPlanEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void deletePlan(Long planId) {
        this.removeById(planId);
    }
}
