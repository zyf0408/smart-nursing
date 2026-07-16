package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.VideoDto;
import com.smart.nursing.entity.VideoEntity;
import com.smart.nursing.vo.VideoVo;

/**
 * 护理视频 Service 接口
 */
public interface IVideoService extends IService<VideoEntity> {

    /**
     * 分页条件查询视频
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<VideoEntity> listVideoByCondition(VideoDto dto);

    /**
     * 根据ID获取视频详情
     *
     * @param videoId 视频ID
     * @return 视频详情 VO
     */
    VideoVo getVideoById(Long videoId);

    /**
     * 新增视频
     *
     * @param dto 视频信息
     */
    void addVideo(VideoDto dto);

    /**
     * 修改视频
     *
     * @param dto 视频信息
     */
    void updateVideo(VideoDto dto);

    /**
     * 删除视频
     *
     * @param videoId 视频ID
     */
    void deleteVideo(Long videoId);
}
