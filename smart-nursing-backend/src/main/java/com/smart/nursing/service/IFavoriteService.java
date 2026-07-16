package com.smart.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.entity.FavoriteEntity;

import java.util.List;

/**
 * 收藏 Service 接口
 */
public interface IFavoriteService extends IService<FavoriteEntity> {

    /**
     * 添加收藏
     *
     * @param userId      用户ID
     * @param contentType 内容类型（1-文章 2-视频 3-课件）
     * @param contentId   内容ID
     */
    void addFavorite(Long userId, Integer contentType, Long contentId);

    /**
     * 取消收藏
     *
     * @param userId      用户ID
     * @param contentType 内容类型
     * @param contentId   内容ID
     */
    void removeFavorite(Long userId, Integer contentType, Long contentId);

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<FavoriteEntity> getFavoriteList(Long userId);

    /**
     * 判断是否已收藏
     *
     * @param userId      用户ID
     * @param contentType 内容类型
     * @param contentId   内容ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Integer contentType, Long contentId);
}
