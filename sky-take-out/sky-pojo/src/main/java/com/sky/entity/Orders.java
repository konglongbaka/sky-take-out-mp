package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 订单表
 * @TableName orders
 */
@TableName(value ="orders")
@Data
public class Orders  implements Serializable {
    public static final Integer PENDING_PAYMENT = 1;
    public static final Integer TO_BE_CONFIRMED = 2;
    public static final Integer CONFIRMED = 3;
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    public static final Integer COMPLETED = 5;
    public static final Integer CANCELLED = 6;

    /**
     * 支付状态 0未支付 1已支付 2退款
     */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUND = 2;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField(value = "number")
    private String number;

    /**
     * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 下单用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 地址id
     */
    @TableField(value = "address_book_id")
    private Long addressBookId;

    /**
     * 下单时间
     */
    @TableField(value = "order_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;

    /**
     * 结账时间
     */
    @TableField(value = "checkout_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkoutTime;

    /**
     * 支付方式 1微信,2支付宝
     */
    @TableField(value = "pay_method")
    private Integer payMethod;

    /**
     * 支付状态 0未支付 1已支付 2退款
     */
    @TableField(value = "pay_status")
    private Integer payStatus;

    /**
     * 实收金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 用户名称
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 收货人
     */
    @TableField(value = "consignee")
    private String consignee;

    /**
     * 订单取消原因
     */
    @TableField(value = "cancel_reason")
    private String cancelReason;

    /**
     * 订单拒绝原因
     */
    @TableField(value = "rejection_reason")
    private String rejectionReason;

    /**
     * 订单取消时间
     */
    @TableField(value = "cancel_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;

    /**
     * 预计送达时间
     */
    @TableField(value = "estimated_delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date estimatedDeliveryTime;

    /**
     * 配送状态  1立即送出  0选择具体时间
     */
    @TableField(value = "delivery_status")
    private Integer deliveryStatus;

    /**
     * 送达时间
     */
    @TableField(value = "delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;

    /**
     * 打包费
     */
    @TableField(value = "pack_amount")
    private Integer packAmount;

    /**
     * 餐具数量
     */
    @TableField(value = "tableware_number")
    private Integer tablewareNumber;

    /**
     * 餐具数量状态  1按餐量提供  0选择具体数量
     */
    @TableField(value = "tableware_status")
    private Integer tablewareStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}