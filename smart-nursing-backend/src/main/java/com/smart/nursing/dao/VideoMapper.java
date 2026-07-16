package com.smart.nursing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.dto.VideoDto;
import com.smart.nursing.entity.VideoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 护理视频 Mapper
 */
public interface VideoMapper extends BaseMapper<VideoEntity> {

    /**
     * 视频分页查询（支持标题模糊查询 + 分类筛选）
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<VideoEntity> selectVideoPage(Page<VideoEntity> page, @Param("dto") VideoDto dto);
}
