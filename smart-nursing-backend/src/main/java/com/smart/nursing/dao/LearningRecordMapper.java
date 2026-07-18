package com.smart.nursing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.dto.LearningRecordDto;
import com.smart.nursing.entity.LearningRecordEntity;
import com.smart.nursing.vo.LearningRecordVo;
import org.apache.ibatis.annotations.Param;

/**
 * 学习记录 Mapper
 */
public interface LearningRecordMapper extends BaseMapper<LearningRecordEntity> {

    /**
     * 学习记录分页查询（关联用户名与内容标题）
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<LearningRecordVo> selectRecordPage(Page<LearningRecordVo> page, @Param("dto") LearningRecordDto dto);
}
