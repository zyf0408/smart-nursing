package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.FavoriteMapper;
import com.smart.nursing.entity.FavoriteEntity;
import com.smart.nursing.service.IFavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏 Service 实现类
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, FavoriteEntity> implements IFavoriteService {

    @Override
    public void addFavorite(Long userId, Integer contentType, Long contentId) {
        // 检查是否已收藏
        if (isFavorite(userId, contentType, contentId)) {
            return;
        }
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setUserId(userId);
        favorite.setContentType(contentType);
        favorite.setContentId(contentId);
        this.save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Integer contentType, Long contentId) {
        this.remove(new LambdaQueryWrapper<FavoriteEntity>()
                .eq(FavoriteEntity::getUserId, userId)
                .eq(FavoriteEntity::getContentType, contentType)
                .eq(FavoriteEntity::getContentId, contentId));
    }

    @Override
    public List<FavoriteEntity> getFavoriteList(Long userId) {
        return this.list(new LambdaQueryWrapper<FavoriteEntity>()
                .eq(FavoriteEntity::getUserId, userId)
                .orderByDesc(FavoriteEntity::getCreateTime));
    }

    @Override
    public boolean isFavorite(Long userId, Integer contentType, Long contentId) {
        long count = this.count(new LambdaQueryWrapper<FavoriteEntity>()
                .eq(FavoriteEntity::getUserId, userId)
                .eq(FavoriteEntity::getContentType, contentType)
                .eq(FavoriteEntity::getContentId, contentId));
        return count > 0;
    }
}
