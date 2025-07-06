package com.sky.admin.controller;

import com.sky.entity.TbVoucher;
import com.sky.result.Result;
import com.sky.service.TbVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tb")
public class TbController {
    @Autowired
    private TbVoucherService voucherService;
    /**
     * 新增普通券
     * @param voucher 优惠券信息
     * @return 优惠券id
     */
    @PostMapping
    public Result<String> addVoucher(@RequestBody TbVoucher voucher) {
        voucherService.save(voucher);
        return Result.success(voucher.getId().toString());
    }
    /**
     * 新增秒杀券
     * @param voucher 优惠券信息，包含秒杀信息
     * @return 优惠券id
     */
    @PostMapping("seckill")
    public Result addSeckillVoucher(@RequestBody TbVoucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.success(voucher.getId());
    }
    //秒杀
    @PostMapping("seckill/{id}")
    public Result<String> seckillVoucher(@PathVariable("id") Long voucherId) throws InterruptedException {
        return voucherService.seckillVoucher(voucherId);
    }
}
