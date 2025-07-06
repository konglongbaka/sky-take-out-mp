package com.sky.aop;

import com.sky.context.BaseContext;
import com.sky.entity.LogTable;
import com.sky.result.Result;
import com.sky.service.LogTableService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
@Slf4j
public class LoggerAop {
    @Autowired
    private LogTableService logService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String LOG_QUEUE = "LogQueue";

    @Pointcut("@annotation(com.sky.anno.Log)")
    public void log() {
    }
//
//    @AfterReturning(pointcut = "log()",returning = "result")
//    public void logAround(JoinPoint joinPoint, Object result) {
//        LogTable logEntity = new LogTable();
//        logEntity.setCreateTime(new Date());
//        logEntity.setMethodName(joinPoint.getSignature().getName());
//        logEntity.setClassName(joinPoint.getTarget().getClass().getName());
//        logEntity.setParams(Arrays.toString(joinPoint.getArgs()));
//        logEntity.setUserId(BaseContext.getCurrentId());
//        logEntity.setLogMessage("成功");
//        logEntity.setReturnMessage(result.toString());
//        logService.saveLog(logEntity);
//    }
//
//    @AfterThrowing(pointcut = "log()", throwing = "ex")
//    public void logAfterThrowing(JoinPoint joinPoint,Throwable ex){
//        LogTable logEntity = new LogTable();
//        logEntity.setCreateTime(new Date());
//        logEntity.setMethodName(joinPoint.getSignature().getName());
//        logEntity.setClassName(joinPoint.getTarget().getClass().getName());
//        logEntity.setParams(Arrays.toString(joinPoint.getArgs()));
//        logEntity.setUserId(BaseContext.getCurrentId());
//        logEntity.setLogMessage("失败");
//        logEntity.setReturnMessage(ex.getMessage());
//        log.info("异常信息：{}", logEntity);
//        logService.saveLog(logEntity);
//    }
    //异步记录日志
    @Around("log()")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        LogTable logEntity = new LogTable();
        logEntity.setCreateTime(new Date());
        logEntity.setMethodName(joinPoint.getSignature().getName());
        logEntity.setClassName(joinPoint.getTarget().getClass().getName());
        logEntity.setParams(Arrays.toString(joinPoint.getArgs()));
        logEntity.setUserId(BaseContext.getCurrentId());
        try {
            Object result = joinPoint.proceed();
            logEntity.setLogMessage("成功");
            logEntity.setReturnMessage(result.toString());
            rabbitTemplate.convertAndSend(LOG_QUEUE, logEntity);
            return result;
        } catch (Throwable e) {
            logEntity.setLogMessage("失败");
            logEntity.setReturnMessage(e.getMessage());
            rabbitTemplate.convertAndSend(LOG_QUEUE, logEntity);
            throw new RuntimeException(e.getMessage());
        }
    }
}

