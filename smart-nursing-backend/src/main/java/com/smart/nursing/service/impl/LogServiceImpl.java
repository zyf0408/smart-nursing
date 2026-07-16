package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.LogMapper;
import com.smart.nursing.entity.LogEntity;
import com.smart.nursing.service.ILogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统操作日志 Service 实现类
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity> implements ILogService {

    @Override
    public IPage<LogEntity> listLogByCondition(Page<LogEntity> page, String username, Integer logType) {
        LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), LogEntity::getUsername, username);
        wrapper.eq(logType != null, LogEntity::getLogType, logType);
        wrapper.orderByDesc(LogEntity::getCreateTime);
        return this.page(page, wrapper);
    }
}
