package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.*;
import com.smart.nursing.dto.ArticleDto;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.IArticleService;
import com.smart.nursing.vo.ArticleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 护理文章 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity> implements IArticleService {

    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<ArticleEntity> listArticleByCondition(ArticleDto dto) {
        Page<ArticleEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return baseMapper.selectArticlePage(page, dto);
    }

    @Override
    public ArticleVo getArticleById(Long articleId) {
        // 1. 查文章
        ArticleEntity article = this.getById(articleId);
        if (article == null) {
            return null;
        }
        ArticleVo vo = new ArticleVo();
        vo.setArticleId(article.getArticleId());
        vo.setTitle(article.getTitle());
        vo.setCategoryId(article.getCategoryId());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCoverImage(article.getCoverImage());
        vo.setAuthorId(article.getAuthorId());
        vo.setViewCount(article.getViewCount());
        vo.setSortOrder(article.getSortOrder());
        vo.setStatus(article.getStatus());
        vo.setCreateTime(article.getCreateTime());
        // 2. 查类别名
        if (article.getCategoryId() != null) {
            CategoryEntity category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }
        // 3. 查作者名
        if (article.getAuthorId() != null) {
            UserEntity author = userMapper.selectById(article.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getRealName());
            }
        }
        // 4. 查标签列表
        List<ArticleTagEntity> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getArticleId, articleId));
        if (!CollectionUtils.isEmpty(articleTags)) {
            List<Long> tagIds = articleTags.stream()
                    .map(ArticleTagEntity::getTagId)
                    .collect(Collectors.toList());
            List<TagEntity> tagList = tagMapper.selectBatchIds(tagIds);
            vo.setTagList(tagList);
        } else {
            vo.setTagList(new ArrayList<>());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addArticle(ArticleDto dto) {
        // 1. 插入文章
        if (dto.getViewCount() == null) {
            dto.setViewCount(0);
        }
        if (dto.getSortOrder() == null) {
            dto.setSortOrder(0);
        }
        if (dto.getStatus() == null) {
            dto.setStatus(1);
        }
        this.save(dto);
        // 2. 保存标签关联 (article_tag)
        saveArticleTags(dto.getArticleId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleDto dto) {
        // 1. 更新文章
        this.updateById(dto);
        // 2. 删除旧标签关联
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getArticleId, dto.getArticleId()));
        // 3. 保存新标签关联
        saveArticleTags(dto.getArticleId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long articleId) {
        this.removeById(articleId);
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getArticleId, articleId));
    }

    @Override
    public void publishArticle(Long articleId) {
        ArticleEntity article = this.getById(articleId);
        if (article != null) {
            // 切换发布状态：已上架(1)→下架(0)，下架(0)→上架(1)
            Integer currentStatus = article.getStatus();
            article.setStatus(currentStatus != null && currentStatus == 1 ? 0 : 1);
            this.updateById(article);
        }
    }

    /**
     * 保存文章-标签关联
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        for (Long tagId : tagIds) {
            ArticleTagEntity articleTag = new ArticleTagEntity();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }
}
