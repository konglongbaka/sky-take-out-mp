package com.sky.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.vo.OrderPageVO;

/**
* @author tx
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2025-06-04 16:07:40
* @Entity com.sky.entity.Orders
*/
public interface OrdersMapper extends BaseMapper<Orders> {

    IPage<OrderPageVO> pageHistoryOrdersQuery(IPage<OrderPageVO> page, OrdersPageQueryDTO ordersPageQueryDTO);
}




