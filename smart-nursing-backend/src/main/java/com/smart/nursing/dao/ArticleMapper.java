package com.smart.nursing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.nursing.dto.ArticleDto;
import com.smart.nursing.entity.ArticleEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 护理文章 Mapper
 */
public interface ArticleMapper extends BaseMapper<ArticleEntity> {

    /**
     * 文章分页查询（支持标题模糊查询 + 分类筛选）
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<ArticleEntity> selectArticlePage(Page<ArticleEntity> page, @Param("dto") ArticleDto dto);
}
