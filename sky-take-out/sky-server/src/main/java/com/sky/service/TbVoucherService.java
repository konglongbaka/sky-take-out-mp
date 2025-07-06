package com.sky.service;

import com.sky.entity.TbVoucher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.result.Result;

/**
* @author tx
* @description 针对表【tb_voucher】的数据库操作Service
* @createDate 2025-06-10 10:39:21
*/
public interface TbVoucherService extends IService<TbVoucher> {

    void addSeckillVoucher(TbVoucher voucher);

    Result<String> seckillVoucher(Long voucherId) throws InterruptedException;

}
