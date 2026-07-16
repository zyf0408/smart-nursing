package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.entity.LogEntity;

/**
 * 系统操作日志 Service 接口
 */
public interface ILogService extends IService<LogEntity> {

    /**
     * 分页条件查询日志
     *
     * @param page     分页参数
     * @param username 操作用户名（模糊查询）
     * @param logType  日志类型
     * @return 分页结果
     */
    IPage<LogEntity> listLogByCondition(Page<LogEntity> page, String username, Integer logType);
}
