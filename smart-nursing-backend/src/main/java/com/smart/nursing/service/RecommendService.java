package com.smart.nursing.service;

import java.util.List;
import java.util.Map;

/**
 * 个性化推荐 Service
 */
public interface RecommendService {

    /**
     * 基于用户学习记录和考试记录，推荐相关学习内容
     *
     * @param userId 用户ID
     * @return 推荐内容列表
     */
    List<Map<String, Object>> recommend(Long userId);
}
