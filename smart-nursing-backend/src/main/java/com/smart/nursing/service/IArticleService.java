package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.ArticleDto;
import com.smart.nursing.entity.ArticleEntity;
import com.smart.nursing.vo.ArticleVo;

/**
 * 护理文章 Service 接口
 */
public interface IArticleService extends IService<ArticleEntity> {

    /**
     * 分页条件查询文章
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<ArticleEntity> listArticleByCondition(ArticleDto dto);

    /**
     * 根据ID获取文章详情
     *
     * @param articleId 文章ID
     * @return 文章详情 VO
     */
    ArticleVo getArticleById(Long articleId);

    /**
     * 新增文章
     *
     * @param dto 文章信息
     */
    void addArticle(ArticleDto dto);

    /**
     * 修改文章
     *
     * @param dto 文章信息
     */
    void updateArticle(ArticleDto dto);

    /**
     * 删除文章
     *
     * @param articleId 文章ID
     */
    void deleteArticle(Long articleId);

    /**
     * 发布/下架文章
     *
     * @param articleId 文章ID
     */
    void publishArticle(Long articleId);
}
