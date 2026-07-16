package com.smart.nursing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.dto.ExamRecordDto;
import com.smart.nursing.entity.ExamRecordEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 考试记录 Mapper
 */
public interface ExamRecordMapper extends BaseMapper<ExamRecordEntity> {

    /**
     * 考试记录分页查询（关联用户名与考试名）
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<ExamRecordEntity> selectRecordPage(Page<ExamRecordEntity> page, @Param("dto") ExamRecordDto dto);
}
