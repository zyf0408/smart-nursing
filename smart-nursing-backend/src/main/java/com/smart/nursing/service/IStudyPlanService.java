package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.StudyPlanDto;
import com.smart.nursing.entity.StudyPlanEntity;

/**
 * 学习计划 Service 接口
 */
public interface IStudyPlanService extends IService<StudyPlanEntity> {

    /**
     * 分页条件查询学习计划
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<StudyPlanEntity> listPlanByCondition(StudyPlanDto dto);

    /**
     * 新增学习计划
     *
     * @param entity 计划信息
     */
    void addPlan(StudyPlanEntity entity);

    /**
     * 修改学习计划
     *
     * @param entity 计划信息
     */
    void updatePlan(StudyPlanEntity entity);

    /**
     * 删除学习计划
     *
     * @param planId 计划ID
     */
    void deletePlan(Long planId);
}
