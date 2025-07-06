package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.OrderWebSocket;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.mapper.*;
import com.sky.service.OrderDetailService;
import com.sky.service.OrdersService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderPageVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tx
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2025-06-04 16:07:40
 */
@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.传入order表中
        Orders orders = new Orders();
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
        User user = userMapper.selectById(BaseContext.getCurrentId());
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        String number = LocalDateTime.now().format( DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + new AtomicInteger(0).incrementAndGet()%100;
        //生成订单号
        orders.setNumber(number);
        //生成实体对象，设置属性
        orders.setUserId(BaseContext.getCurrentId());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserName(user.getName());
        orders.setOrderTime(new Date());
        orders.setEstimatedDeliveryTime(new Date());
        orders.setPhone(addressBook.getPhone());
        String address = addressBook.getProvinceName()  + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);
        ordersMapper.insert(orders);

        //2.传入orderDetail表中
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, BaseContext.getCurrentId()));
        shoppingCarts.forEach(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetail.setId(null);
            orderDetailMapper.insert(orderDetail);
        });
        return OrderSubmitVO.builder().id(orders.getId()).orderTime(LocalDateTime.now()).orderNumber(orders.getNumber()).orderAmount(orders.getAmount()).build();
    }

    @Override
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>().eq(Orders::getNumber, outTradeNo));

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        ordersDB.setStatus(Orders.TO_BE_CONFIRMED);
        ordersDB.setPayStatus(Orders.PAID);
        ordersDB.setCheckoutTime(new Date());
        ordersMapper.updateById(ordersDB);
    }
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);

        Orders orders = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>().eq(Orders::getNumber, ordersPaymentDTO.getOrderNumber()));

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单

        //发现没有将支付时间 check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();

        //获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orders.setPayStatus(OrderPaidStatus);
        orders.setCheckoutTime(new Date());
        orders.setStatus(OrderStatus);
        orders.setNumber(orderNumber);
        ordersMapper.updateById(orders);


        Map map = new HashMap();
        map.put("orderId", orders.getId());
        map.put("type", OrderWebSocket.ORDER_PAYMENT);//来单
        map.put("content", "用户下单"+orders.getNumber());
        String message = JSON.toJSONString(map);
//        webSocketServer.sendToAllClient(message);

        return vo;
    }

    @Override
    public void repetition(Long id) {
        Orders orders = ordersMapper.selectById(id);
        List<OrderDetail> list = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, id));
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        list.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(new Date());
            shoppingCart.setId(null);
            shoppingCarts.add(shoppingCart);
        });
        shoppingCartService.saveBatch(shoppingCarts);
    }

    @Override
    public void reminder(Long id) {
        //
        log.info("催单");
        Orders orders = ordersMapper.selectById(id);
        Map map = new HashMap();
        map.put("orderId", orders.getId());
        map.put("type", OrderWebSocket.ORDER_REMINDER);//催单
        map.put("content", "用户催单"+orders.getNumber());
        String message = JSON.toJSONString(map);
//        webSocketServer.sendToAllClient(message);
    }

    @Override
    public IPage<OrderPageVO> historyOrdersPage(IPage<OrderPageVO> page, OrdersPageQueryDTO ordersPageQueryDTO) {
        return ordersMapper.pageHistoryOrdersQuery(page, ordersPageQueryDTO);
    }


}




