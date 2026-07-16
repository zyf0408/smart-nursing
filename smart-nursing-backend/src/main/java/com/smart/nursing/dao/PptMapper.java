package com.smart.nursing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.dto.PptDto;
import com.smart.nursing.entity.PptEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 护理课件 Mapper
 */
public interface PptMapper extends BaseMapper<PptEntity> {

    /**
     * 课件分页查询（支持标题模糊查询 + 分类筛选）
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<PptEntity> selectPptPage(Page<PptEntity> page, @Param("dto") PptDto dto);
}
