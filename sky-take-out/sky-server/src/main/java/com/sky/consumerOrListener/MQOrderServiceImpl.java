package com.sky.consumerOrListener;

import com.alibaba.fastjson.JSON;
import com.sky.entity.TbVoucherOrder;
import com.sky.mapper.TbVoucherOrderMapper;
import com.sky.service.TbSeckillVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MQOrderServiceImpl implements MQService{
    @Autowired
    private TbSeckillVoucherService seckillVoucherService;
    @Autowired
    private TbVoucherOrderMapper voucherOrderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    public static final String QUEUE_KEY = "queue:order:";
    @Override
    public void sendMessage(TbVoucherOrder order) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, JSON.toJSONString(order));
        System.out.println("订单正在发送" + order);
    }

    //发送消息
//
    @Override
    public void receiveMessage() throws InterruptedException {
        TbVoucherOrder voucherOrder = JSON.parseObject(redisTemplate.opsForList().rightPop(QUEUE_KEY) ,TbVoucherOrder.class);
        if (voucherOrder == null){
            Thread.sleep(20);
        }

        if (voucherOrder != null)
        {
            seckillVoucherService.update().
                    setSql("stock = stock - 1").
                    eq("voucher_id", voucherOrder.getVoucherId()).
                    gt("stock", 0).        // 修改判断逻辑，改为只要库存大于0，就允许线程扣减
                    update();
            voucherOrderMapper.insert(voucherOrder);
        }
    }
}
