package com.smart.nursing.service;

import com.smart.nursing.vo.DashboardVo;

/**
 * 数据统计 Service 接口
 */
public interface IDataCountService {

    /**
     * 获取首页仪表盘统计数据
     *
     * @return 仪表盘 VO
     */
    DashboardVo getDashboardData();
}
