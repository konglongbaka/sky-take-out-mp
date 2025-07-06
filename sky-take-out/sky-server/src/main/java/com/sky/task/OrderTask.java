package com.sky.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderTask {
    @Scheduled(cron = "0 0/1 * * * ?")// 每分钟执行一次
    public void removeTimeoutOrders(){

    }

    // 每1秒执行一次
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void removeRepeatSubmitOrders(){
//        System.out.println("定时任务启动了");
//    }
}
