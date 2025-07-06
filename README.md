# 外卖订餐系统 - 后台管理 + 用户平台

## 项目简介
本项目是一个基于SpringBoot的全栈式外卖订餐系统，包含**后台管理端**和**用户端**两大模块。管理端提供商品分类管理、订单处理、员工管理、数据统计和优惠券发放等功能；用户端支持菜单浏览、下单支付、订单状态跟踪等核心业务。系统采用分布式架构设计，通过Redis、RabbitMQ等技术保障高并发场景下的性能与数据一致性。

## 技术栈
| 分类        | 技术组件                 |
|-------------|--------------------------|
| 核心框架    | SpringBoot 3.x           |
| 数据存储    | MySQL 8.0 + Redis 6.x    |
| 数据访问    | MyBatis-Plus             |
| 安全认证    | JWT                      |
| 消息队列    | RabbitMQ                 |
| 接口文档    | Swagger 3.0              |
| 其他工具    | Lombok + Hutool          |

## 系统功能模块
### 🖥️ 管理端功能
- **商品管理**：分类管理/菜品上下架/套餐管理
- **订单管理**：订单查询/状态修改/出餐管理
- **员工管理**：角色权限控制/账户状态管理
- **数据统计**：销售额分析/订单量趋势/用户画像
- **营销中心**：优惠券创建/发放/核销管理

### 📱 用户端功能
- **浏览功能**：菜品分类浏览/菜品详情展示
- **购物车**：多商品管理/数量修改
- **订单系统**：在线支付/订单状态跟踪
- **个人中心**：历史订单查询/收货地址管理
- **优惠活动**：领取优惠券/查看可用优惠

## ✨对于苍穹外卖的优化

### 1. Redis高并发常见问题优化
```java
// 简单代码示例：缓存穿透+雪崩+击穿解决方案
@GetMapping("/{id}")
    @Cacheable(cacheNames = CacheNamesConstant.ADMINEMP + "id:", key = "#id")
    public Result<Employee> getById(@PathVariable Long id) throws InterruptedException {
        String key = CacheNamesConstant.ADMINEMP + id;
        String json = (String) redisTemplate.opsForValue().get(key);
        if(json!=null){
            if (json.isEmpty()){
                return Result.error("未找到该员工");
            }
            Employee jsonObject= JSON.parseObject(json, Employee.class);
            return Result.success(jsonObject);
        }
        String lockKey = CacheNamesConstant.ADMINEMP + "lockKey:" + id;
        RLock rLock = redissionConfig.redissonClient().getLock(lockKey);
        boolean lock = rLock.tryLock(1,20, TimeUnit.SECONDS);
        if (!lock){
            return Result.error("请稍后再试");
        }
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            redisTemplate.opsForValue().set(key,"", 1000, TimeUnit.SECONDS);
            rLock.unlock();
            return Result.error("未找到该员工");
        }
        //设置过期时间随机
        redisTemplate.opsForValue().set(key,JSON.toJSONString(employee), RandomTimeUtil.getRandom(),TimeUnit.SECONDS);
        rLock.unlock();
        return Result.success(employee);
    }
```

### 2. 分布式一致性解决方案
- **库存超卖防护**
- **一人一单实现**
- 仅基于乐观锁加互斥锁版本
```java
 普通秒杀，查询数据库版本
    @Override
    public Result<String> seckillVoucher(Long voucherId) throws InterruptedException {
        // 1.查询优惠券
        TbSeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // 2.判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.error("秒杀尚未开始!");
        }
        // 3.判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // 尚未结束
            return Result.error("秒杀已结束！");
        }
        // 4.判断库存是否充足
        if (voucher.getStock() < 1) {
            // 库存不足
            return Result.error("库存不足!");
        }
        //5.加锁并判断是否多下单
        Long userId = BaseContext.getCurrentId();
        String lockKey = "rLock:voucher:" + "voucherId:" + voucherId + "user:" + userId;
        RLock rLock = redissonClient.getLock(lockKey);
        boolean b = rLock.tryLock();
        if (!b)
        {
            return Result.error("请勿重复下单");
        }
        try {
            TbVoucherService proxy = (TbVoucherService) AopContext.currentProxy();
            return proxy.getStringResult(voucherId);
        } finally {
            rLock.unlock();
        }
    }
    @Transactional
    public Result<String> getStringResult(Long voucherId) {
        // 查询优惠券，判断是否具有资格
        Long userId = BaseContext.getCurrentId();
        long count = tbVoucherOrderService.count(new LambdaQueryWrapper<TbVoucherOrder>()
                .eq(TbVoucherOrder::getVoucherId, voucherId)
                .eq(TbVoucherOrder::getUserId, userId));
        if (count > 0) return Result.error("已购买");
        // 5.扣减库存
        boolean success = seckillVoucherService.update().
                setSql("stock = stock - 1").
                eq("voucher_id", voucherId).
                gt("stock", 0).        // 修改判断逻辑，改为只要库存大于0，就允许线程扣减
                        update();
        if (!success) {
            throw new RuntimeException("库存不足");
        }
        //6. 创建订单
        TbVoucherOrder voucherOrder = new TbVoucherOrder();
        //6.1 设置订单id
        long orderId = redisIdWorker.nextId("order");
        //6.2 设置用户id
        //6.3 设置代金券id
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(BaseContext.getCurrentId());
        //7. 将订单数据保存到表中
        voucherOrderMapper.insert(voucherOrder);
        //8. 返回订单id
        return Result.success(String.valueOf(orderId));
    }
```
- **基于Redis原子操作与RabbitMQ版本**
```java
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
```
> *注：优惠券发放采用异步化处理后，接口响应速度提升显著

### 3. 安全与效率优化
- **无状态认证**：JWT Token设计
  ```
  Header: { "alg": "HS256", "typ": "JWT" }
  Payload: { "userId": 123, "role": "ADMIN" }
  ```
- **全局异常处理**：统一返回格式
  ```json
  {
    "code": 500,
    "msg": "${e.getmessage}",
    "data": null
  }
  ```
