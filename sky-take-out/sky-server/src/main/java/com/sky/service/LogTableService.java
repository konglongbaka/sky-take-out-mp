package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.LogTable;

/**
* @author tx
* @description 针对表【log_table】的数据库操作Service
* @createDate 2025-06-12 16:00:57
*/
public interface LogTableService extends IService<LogTable> {

    void saveLog(LogTable logEntity);
}
