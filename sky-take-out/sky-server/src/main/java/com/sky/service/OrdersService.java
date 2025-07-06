package com.sky.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderPageVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

/**
* @author tx
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2025-06-04 16:07:40
*/
public interface OrdersService extends IService<Orders> {

    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    void paySuccess(String outTradeNo);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);


    void repetition(Long id);

    void reminder(Long id);

    IPage<OrderPageVO> historyOrdersPage(IPage<OrderPageVO> page, OrdersPageQueryDTO ordersPageQueryDTO);
}
