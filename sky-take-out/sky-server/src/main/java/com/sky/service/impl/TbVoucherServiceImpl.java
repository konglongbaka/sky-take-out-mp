package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.consumerOrListener.MQService;
import com.sky.context.BaseContext;
import com.sky.entity.TbSeckillVoucher;
import com.sky.entity.TbVoucher;
import com.sky.entity.TbVoucherOrder;
import com.sky.mapper.TbVoucherMapper;
import com.sky.mapper.TbVoucherOrderMapper;
import com.sky.result.Result;
import com.sky.service.TbSeckillVoucherService;
import com.sky.service.TbVoucherOrderService;
import com.sky.service.TbVoucherService;
import com.sky.utils.RedisIdUtil;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tx
 * @description 针对表【tb_voucher】的数据库操作Service实现
 * @createDate 2025-06-10 10:39:21
 */
@Service
public class TbVoucherServiceImpl extends ServiceImpl<TbVoucherMapper, TbVoucher>
        implements TbVoucherService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private TbSeckillVoucherService seckillVoucherService;
    @Autowired
    private TbVoucherOrderService tbVoucherOrderService;
    @Autowired
    private RedisIdUtil redisIdWorker;
    @Autowired
    private TbVoucherMapper voucherMapper;
    @Autowired
    private TbVoucherOrderMapper voucherOrderMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MQService mqService;


    private static final String STOCK_KEY = "stock:voucher:";
    private static final String SECKILL_KEY = "seckill:voucher:";
    private static final String SECKILL_HAVE_KEY = "seckillHave:voucher:";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();


    //TODO
    // 添加优惠券操作改为刷新优惠券方便测试
    @Override
    public void addSeckillVoucher(TbVoucher voucher) {
        // 保存优惠券
//        save(voucher);
        // 保存秒杀信息
        TbSeckillVoucher seckillVoucher = new TbSeckillVoucher();
        // 关联普通券id
        seckillVoucher.setId(voucher.getId());
        seckillVoucher.setVoucherId(voucher.getId());
        // 设置库存
        seckillVoucher.setStock(1000);
        // 设置开始时间
        seckillVoucher.setBeginTime(LocalDateTime.now());
        // 设置结束时间
        seckillVoucher.setEndTime(LocalDateTime.now().plusDays(1000));
        // 保存信息到秒杀券表中
//        seckillVoucherService.save(seckillVoucher);
        seckillVoucherService.updateById(seckillVoucher);
        voucherOrderMapper.delete(new QueryWrapper<TbVoucherOrder>().eq("voucher_id", voucher.getId()));
        stringRedisTemplate.delete("seckillHave:voucher:" + voucher.getId());
        stringRedisTemplate.opsForValue().set("seckill:voucher:" + voucher.getId(), JSON.toJSONString(seckillVoucher));
        stringRedisTemplate.opsForValue().set("stock:voucher:" + voucher.getId(),seckillVoucher.getStock().toString());

    }

//    异步秒杀，RabbitMQ
    @Override
    public Result<String> seckillVoucher(Long voucherId) {
        // 1.查询优惠券是否存在
        TbSeckillVoucher voucher = JSON.parseObject(stringRedisTemplate.opsForValue().get("seckill:voucher:" + voucherId), TbSeckillVoucher.class);
        if (voucher == null) {
            return Result.error("秒杀活动不存在!");
        }
        // 2. 时间校验
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getBeginTime())) {
            return Result.error("秒杀尚未开始!");
        }
        if (now.isAfter(voucher.getEndTime())) {
            return Result.error("秒杀已结束!");
        }
        // 3. Redis预减库存（原子操作）
        String stockKey = "stock:voucher:" + voucherId;
        Long stock = stringRedisTemplate.opsForValue().decrement(stockKey);
        // 处理库存对应情况
        if (stock == null) {
            return Result.error("库存信息异常!");
        }
        if (stock < 0) {
            // 恢复库存并返回错误
            stringRedisTemplate.opsForValue().increment(stockKey);
            return Result.error("库存不足!");
        }
        //4.判断用户是否重复下单
        Long add = stringRedisTemplate.opsForSet().add("seckillHave:voucher:" + voucherId, String.valueOf(BaseContext.getCurrentId()));
        if (add == 0) {
            stringRedisTemplate.opsForValue().increment(stockKey);
            return Result.error("不可重复抢购");
        }
        //5.进行下单
        long orderId = redisIdWorker.nextId("order");
        TbVoucherOrder voucherOrder = new TbVoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(BaseContext.getCurrentId());
        //6.发送对象给rabbit队列
        String queueName = "OrderQueue";
        rabbitTemplate.convertAndSend(queueName,voucherOrder);
        //7. 返回订单id
        return Result.success(String.valueOf(orderId));
    }



//    //异步秒杀，基于Redis List队列
//    @Override
//    public Result<String> seckillVoucher(Long voucherId) {
//        // 1.查询优惠券是否存在
//        TbSeckillVoucher voucher = JSON.parseObject(stringRedisTemplate.opsForValue().get("seckill:voucher:" + voucherId), TbSeckillVoucher.class);
//        if (voucher == null) {
//            return Result.error("秒杀活动不存在!");
//        }
//        // 2. 时间校验
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isBefore(voucher.getBeginTime())) {
//            return Result.error("秒杀尚未开始!");
//        }
//        if (now.isAfter(voucher.getEndTime())) {
//            return Result.error("秒杀已结束!");
//        }
//        // 3. Redis预减库存（原子操作）
//        String stockKey = "stock:voucher:" + voucherId;
//        Long stock = stringRedisTemplate.opsForValue().decrement(stockKey);
//        // 处理库存对应情况
//        if (stock == null) {
//            return Result.error("库存信息异常!");
//        }
//        if (stock < 0) {
//            // 恢复库存并返回错误
//            stringRedisTemplate.opsForValue().increment(stockKey);
//            return Result.error("库存不足!");
//        }
//        //4.判断用户是否重复下单
//        Long add = stringRedisTemplate.opsForSet().add("seckillHave:voucher:" + voucherId, String.valueOf(BaseContext.getCurrentId()));
//        if (add == 0) {
//            stringRedisTemplate.opsForValue().increment(stockKey);
//            return Result.error("不可重复抢购");
//        }
//        //5.进行下单
//        long orderId = redisIdWorker.nextId("order");
//        TbVoucherOrder voucherOrder = new TbVoucherOrder();
//        voucherOrder.setVoucherId(voucherId);
//        voucherOrder.setId(orderId);
//        voucherOrder.setUserId(BaseContext.getCurrentId());
//        //6.发送对象给list队列
//        mqService.sendMessage(voucherOrder);
//        //7. 返回订单id
//        return Result.success(String.valueOf(orderId));
//    }
//    //线程初始化
//    @PostConstruct
//    public void init() {
//        OrderHandler orderHandler = new OrderHandler();
//        EXECUTOR_SERVICE.submit(orderHandler);
//    }
//    //异步处理订单信息
//    private class OrderHandler implements Runnable{
//        @Override
//        public void run() {
//            while (true){
//                try {
//                    mqService.receiveMessage();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }






    //普通秒杀，查询数据库版本
//    @Override
//    public Result<String> seckillVoucher(Long voucherId) throws InterruptedException {
//        // 1.查询优惠券
//        TbSeckillVoucher voucher = seckillVoucherService.getById(voucherId);
//        // 2.判断秒杀是否开始
//        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
//            // 尚未开始
//            return Result.error("秒杀尚未开始!");
//        }
//        // 3.判断秒杀是否已经结束
//        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
//            // 尚未结束
//            return Result.error("秒杀已结束！");
//        }
//        // 4.判断库存是否充足
//        if (voucher.getStock() < 1) {
//            // 库存不足
//            return Result.error("库存不足!");
//        }
//        //5.加锁并判断是否多下单
//        Long userId = BaseContext.getCurrentId();
//        String lockKey = "rLock:voucher:" + "voucherId:" + voucherId + "user:" + userId;
//        RLock rLock = redissonClient.getLock(lockKey);
//        boolean b = rLock.tryLock();
//        if (!b)
//        {
//            return Result.error("请勿重复下单");
//        }
//        try {
//            TbVoucherService proxy = (TbVoucherService) AopContext.currentProxy();
//            return proxy.getStringResult(voucherId);
//        } finally {
//            rLock.unlock();
//        }
//    }
//    @Transactional
//    public Result<String> getStringResult(Long voucherId) {
//        // 查询优惠券，判断是否具有资格
//        Long userId = BaseContext.getCurrentId();
//        long count = tbVoucherOrderService.count(new LambdaQueryWrapper<TbVoucherOrder>()
//                .eq(TbVoucherOrder::getVoucherId, voucherId)
//                .eq(TbVoucherOrder::getUserId, userId));
//        if (count > 0) return Result.error("已购买");
//        // 5.扣减库存
//        boolean success = seckillVoucherService.update().
//                setSql("stock = stock - 1").
//                eq("voucher_id", voucherId).
//                gt("stock", 0).        // 修改判断逻辑，改为只要库存大于0，就允许线程扣减
//                        update();
//        if (!success) {
//            throw new RuntimeException("库存不足");
//        }
//        //6. 创建订单
//        TbVoucherOrder voucherOrder = new TbVoucherOrder();
//        //6.1 设置订单id
//        long orderId = redisIdWorker.nextId("order");
//        //6.2 设置用户id
//        //6.3 设置代金券id
//        voucherOrder.setVoucherId(voucherId);
//        voucherOrder.setId(orderId);
//        voucherOrder.setUserId(BaseContext.getCurrentId());
//        //7. 将订单数据保存到表中
//        voucherOrderMapper.insert(voucherOrder);
//        //8. 返回订单id
//        return Result.success(String.valueOf(orderId));
//    }
}



