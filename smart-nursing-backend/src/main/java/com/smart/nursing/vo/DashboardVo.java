package com.smart.nursing.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 首页仪表盘统计数据 VO
 */
@Data
public class DashboardVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 文章总数
     */
    private Long articleCount;

    /**
     * 视频总数
     */
    private Long videoCount;

    /**
     * 考试总数
     */
    private Long examCount;

    /**
     * 各分类内容数量统计
     */
    private List<Map<String, Object>> categoryContentCounts;

    /**
     * 学习趋势数据
     */
    private List<Map<String, Object>> learningTrend;

    /**
     * 考试通过率
     */
    private Double examPassRate;
}
