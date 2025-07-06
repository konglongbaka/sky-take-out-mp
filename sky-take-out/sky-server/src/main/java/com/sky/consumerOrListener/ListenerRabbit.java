package com.sky.consumerOrListener;

import com.sky.entity.LogTable;
import com.sky.entity.TbVoucherOrder;
import com.sky.mapper.TbVoucherOrderMapper;
import com.sky.service.LogTableService;
import com.sky.service.TbSeckillVoucherService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ListenerRabbit {
    @Autowired
    private TbSeckillVoucherService seckillVoucherService;
    @Autowired
    private TbVoucherOrderMapper voucherOrderMapper;
    @Autowired
    private LogTableService logService;

    @RabbitListener(queues = "OrderQueue")
    public void voucherOrderListen(TbVoucherOrder voucherOrder){
        seckillVoucherService.update().
                setSql("stock = stock - 1").
                eq("voucher_id", voucherOrder.getVoucherId()).
                gt("stock", 0).        // 修改判断逻辑，改为只要库存大于0，就允许线程扣减
                update();
        voucherOrderMapper.insert(voucherOrder);
    }

    @RabbitListener(queues = "LogQueue")
    public void logListen(LogTable logEntity){
        logService.save(logEntity);
    }
//    @RabbitListener(queues = "SimpleQueue1")
//    public void receiveMessage1(String message){
//        System.out.println("11111111接收到消息11111111："+message);
//    }
//    @RabbitListener(queues = "SimpleQueue1")
//    public void receiveMessage12(String message){
//        System.out.println("222222222接收到消息11111111："+message);
//    }
//    @RabbitListener(queues = "SimpleQueue2")
//    public void receiveMessage2(String message){
//        System.out.println("接收到消息22222222："+message);
//    }
//    @RabbitListener(queues = "ObjectQueue")
//    public void receiveMessage13(Map<String,Object> map){
//        System.out.println("==========接收到对象=========："+ map.get("name"));
//    }

}
