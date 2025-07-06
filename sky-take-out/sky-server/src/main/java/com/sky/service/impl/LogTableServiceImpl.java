package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.entity.LogTable;
import com.sky.service.LogTableService;
import com.sky.mapper.LogTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
* @author tx
* @description 针对表【log_table】的数据库操作Service实现
* @createDate 2025-06-12 16:00:57
*/
@Service
public class LogTableServiceImpl extends ServiceImpl<LogTableMapper, LogTable>
    implements LogTableService{
    @Autowired
    private LogTableMapper logTableMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(LogTable logEntity) {
        logTableMapper.insert(logEntity);
    }
}




