package com.sky.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderDetailService;
import com.sky.service.OrdersService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderPageVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrdersService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;



    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, BaseContext.getCurrentId()));
        //webSocket推送消息
        return Result.success(orderPaymentVO);
    }
    @GetMapping("orderDetail/{id}")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        OrderVO orderVO = new OrderVO();
        Orders order = orderService.getById(id);
        BeanUtils.copyProperties(order,orderVO);
        List<OrderDetail> list = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, id));
        orderVO.setOrderDetailList(list);
        orderVO.setOrderDishes(order.getNumber());
        return Result.success(orderVO);
    }
    @PutMapping("cancel/{id}")
    public Result<String> cancel(@PathVariable Long id){
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.eq(Orders::getId,id);
        ordersLambdaUpdateWrapper.set(Orders::getStatus,Orders.CANCELLED);
        orderService.update(new Orders(),ordersLambdaUpdateWrapper);
        return Result.success("取消成功");
    }

    @GetMapping("/historyOrders")
    public Result<IPage<OrderPageVO>> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        IPage<OrderPageVO> page = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        IPage<OrderPageVO> orderPageVOIPage = orderService.historyOrdersPage(page, ordersPageQueryDTO);
        return Result.success(orderPageVOIPage);
    }

    @PostMapping("repetition/{id}")
    public Result repetition(@PathVariable Long id){
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("reminder/{id}")
    public Result reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }
}
