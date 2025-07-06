package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 套餐菜品关系
 * @TableName setmeal_dish
 */
@TableName(value ="setmeal_dish")
@Data
public class SetmealDish  implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 套餐id
     */
    @TableField(value = "setmeal_id")
    private Long setmealId;

    /**
     * 菜品id
     */
    @TableField(value = "dish_id")
    private Long dishId;

    /**
     * 菜品名称 （冗余字段）
     */
    @TableField(value = "name")
    private String name;

    /**
     * 菜品单价（冗余字段）
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 菜品份数
     */
    @TableField(value = "copies")
    private Integer copies;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}