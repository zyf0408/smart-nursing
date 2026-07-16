package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.*;
import com.smart.nursing.dto.PptDto;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.IPptService;
import com.smart.nursing.vo.PptVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 护理课件（PPT） Service 实现类
 */
@Service
@RequiredArgsConstructor
public class PptServiceImpl extends ServiceImpl<PptMapper, PptEntity> implements IPptService {

    private final PptTagMapper pptTagMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<PptEntity> listPptByCondition(PptDto dto) {
        Page<PptEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return baseMapper.selectPptPage(page, dto);
    }

    @Override
    public PptVo getPptById(Long pptId) {
        PptEntity ppt = this.getById(pptId);
        if (ppt == null) {
            return null;
        }
        PptVo vo = new PptVo();
        vo.setPptId(ppt.getPptId());
        vo.setTitle(ppt.getTitle());
        vo.setCategoryId(ppt.getCategoryId());
        vo.setDescription(ppt.getDescription());
        vo.setFileUrl(ppt.getFileUrl());
        vo.setCoverImage(ppt.getCoverImage());
        vo.setAuthorId(ppt.getAuthorId());
        vo.setViewCount(ppt.getViewCount());
        vo.setSortOrder(ppt.getSortOrder());
        vo.setStatus(ppt.getStatus());
        vo.setCreateTime(ppt.getCreateTime());
        // 查类别名
        if (ppt.getCategoryId() != null) {
            CategoryEntity category = categoryMapper.selectById(ppt.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }
        // 查作者名
        if (ppt.getAuthorId() != null) {
            UserEntity author = userMapper.selectById(ppt.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getRealName());
            }
        }
        // 查标签列表
        List<PptTagEntity> pptTags = pptTagMapper.selectList(new LambdaQueryWrapper<PptTagEntity>()
                .eq(PptTagEntity::getPptId, pptId));
        if (!CollectionUtils.isEmpty(pptTags)) {
            List<Long> tagIds = pptTags.stream()
                    .map(PptTagEntity::getTagId)
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
    public void addPpt(PptDto dto) {
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
        savePptTags(dto.getPptId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePpt(PptDto dto) {
        this.updateById(dto);
        pptTagMapper.delete(new LambdaQueryWrapper<PptTagEntity>()
                .eq(PptTagEntity::getPptId, dto.getPptId()));
        savePptTags(dto.getPptId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePpt(Long pptId) {
        this.removeById(pptId);
        pptTagMapper.delete(new LambdaQueryWrapper<PptTagEntity>()
                .eq(PptTagEntity::getPptId, pptId));
    }

    private void savePptTags(Long pptId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        for (Long tagId : tagIds) {
            PptTagEntity pptTag = new PptTagEntity();
            pptTag.setPptId(pptId);
            pptTag.setTagId(tagId);
            pptTagMapper.insert(pptTag);
        }
    }
}
