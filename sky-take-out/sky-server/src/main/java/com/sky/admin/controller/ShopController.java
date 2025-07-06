package com.sky.admin.controller;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SHOP_STATUS = "shop_status";

    @GetMapping("/status")
    public Result<Object> getStatus() {
        String status = (String) redisTemplate.opsForValue().get(SHOP_STATUS);
        if (status == null) {
            redisTemplate.opsForValue().set(SHOP_STATUS, StatusConstant.ENABLE);
            status = String.valueOf(StatusConstant.ENABLE);
        }
        return Result.success(Integer.parseInt(status));
    }
    @PutMapping("/{status}")
    public Result<String> updateStatus(@PathVariable String status) {
        redisTemplate.opsForValue().set(SHOP_STATUS, status);
        return Result.success();
    }
}
