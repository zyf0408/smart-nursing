package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.*;
import com.smart.nursing.dto.VideoDto;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.IVideoService;
import com.smart.nursing.vo.VideoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 护理视频 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, VideoEntity> implements IVideoService {

    private final VideoTagMapper videoTagMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<VideoEntity> listVideoByCondition(VideoDto dto) {
        Page<VideoEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return baseMapper.selectVideoPage(page, dto);
    }

    @Override
    public VideoVo getVideoById(Long videoId) {
        VideoEntity video = this.getById(videoId);
        if (video == null) {
            return null;
        }
        VideoVo vo = new VideoVo();
        vo.setVideoId(video.getVideoId());
        vo.setTitle(video.getTitle());
        vo.setCategoryId(video.getCategoryId());
        vo.setDescription(video.getDescription());
        vo.setVideoUrl(video.getVideoUrl());
        vo.setCoverImage(video.getCoverImage());
        vo.setDuration(video.getDuration());
        vo.setAuthorId(video.getAuthorId());
        vo.setViewCount(video.getViewCount());
        vo.setSortOrder(video.getSortOrder());
        vo.setStatus(video.getStatus());
        vo.setCreateTime(video.getCreateTime());
        // 查类别名
        if (video.getCategoryId() != null) {
            CategoryEntity category = categoryMapper.selectById(video.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }
        // 查作者名
        if (video.getAuthorId() != null) {
            UserEntity author = userMapper.selectById(video.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getRealName());
            }
        }
        // 查标签列表
        List<VideoTagEntity> videoTags = videoTagMapper.selectList(new LambdaQueryWrapper<VideoTagEntity>()
                .eq(VideoTagEntity::getVideoId, videoId));
        if (!CollectionUtils.isEmpty(videoTags)) {
            List<Long> tagIds = videoTags.stream()
                    .map(VideoTagEntity::getTagId)
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
    public void addVideo(VideoDto dto) {
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
        saveVideoTags(dto.getVideoId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideo(VideoDto dto) {
        this.updateById(dto);
        videoTagMapper.delete(new LambdaQueryWrapper<VideoTagEntity>()
                .eq(VideoTagEntity::getVideoId, dto.getVideoId()));
        saveVideoTags(dto.getVideoId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(Long videoId) {
        this.removeById(videoId);
        videoTagMapper.delete(new LambdaQueryWrapper<VideoTagEntity>()
                .eq(VideoTagEntity::getVideoId, videoId));
    }

    private void saveVideoTags(Long videoId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        for (Long tagId : tagIds) {
            VideoTagEntity videoTag = new VideoTagEntity();
            videoTag.setVideoId(videoId);
            videoTag.setTagId(tagId);
            videoTagMapper.insert(videoTag);
        }
    }
}
