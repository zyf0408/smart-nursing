package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.ArticleTagMapper;
import com.smart.nursing.dao.TagMapper;
import com.smart.nursing.entity.ArticleTagEntity;
import com.smart.nursing.entity.TagEntity;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.exception.BusinessException;
import com.smart.nursing.service.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 护理内容标签 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements ITagService {

    private final ArticleTagMapper articleTagMapper;

    @Override
    public List<TagEntity> getAllTags() {
        return this.list(new LambdaQueryWrapper<TagEntity>()
                .orderByDesc(TagEntity::getCreateTime));
    }

    @Override
    public IPage<TagEntity> listTagByCondition(Page<TagEntity> page, String tagName) {
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(tagName), TagEntity::getTagName, tagName);
        wrapper.orderByDesc(TagEntity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void addTag(TagEntity entity) {
        this.save(entity);
    }

    @Override
    public void updateTag(TagEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void deleteTag(Long tagId) {
        // 检查标签是否被使用
        long useCount = articleTagMapper.selectCount(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getTagId, tagId));
        if (useCount > 0) {
            throw new BusinessException(GlobalErrorCodeConstants.TAG_IN_USE);
        }
        this.removeById(tagId);
    }
}
