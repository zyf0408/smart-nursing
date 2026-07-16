package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.PptDto;
import com.smart.nursing.entity.PptEntity;
import com.smart.nursing.vo.PptVo;

/**
 * 护理课件（PPT） Service 接口
 */
public interface IPptService extends IService<PptEntity> {

    /**
     * 分页条件查询课件
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<PptEntity> listPptByCondition(PptDto dto);

    /**
     * 根据ID获取课件详情
     *
     * @param pptId 课件ID
     * @return 课件详情 VO
     */
    PptVo getPptById(Long pptId);

    /**
     * 新增课件
     *
     * @param dto 课件信息
     */
    void addPpt(PptDto dto);

    /**
     * 修改课件
     *
     * @param dto 课件信息
     */
    void updatePpt(PptDto dto);

    /**
     * 删除课件
     *
     * @param pptId 课件ID
     */
    void deletePpt(Long pptId);
}
