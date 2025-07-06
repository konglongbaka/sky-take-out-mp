package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tx
 * @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
 * @createDate 2025-06-04 16:07:40
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //1.判断是否存在购物车
        ShoppingCart shoppingCart = getShoppingCart(shoppingCartDTO);
        if (ObjectUtils.isNotEmpty(shoppingCart)) {
            Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
            Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
            //2.判断数量
            if (shoppingCart.getNumber() > 1) {
                LambdaQueryWrapper<ShoppingCart> wrapper = getQueryWrapper(shoppingCartDTO);
                BigDecimal newAmount = BigDecimal.valueOf(0.0);
                if (setmeal != null) {
                    newAmount = BigDecimal.valueOf((shoppingCart.getNumber() - 1) * setmeal.getPrice().doubleValue());
                } else if (dish != null) {
                    newAmount = BigDecimal.valueOf((shoppingCart.getNumber() - 1) * dish.getPrice().doubleValue());
                }
                shoppingCart.setAmount(newAmount);
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.update(shoppingCart, wrapper);
            } else {
                deleteShoppingCart(shoppingCartDTO);
            }

        }
    }

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = getShoppingCart(shoppingCartDTO);
        Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
        Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
        //1.判断当前添加是套餐还是菜品
        if (ObjectUtils.isNotEmpty(shoppingCart)) {
            LambdaQueryWrapper<ShoppingCart> wrapper = getQueryWrapper(shoppingCartDTO);
            BigDecimal newAmount = BigDecimal.valueOf(0.0);
            if (setmeal != null) {
                newAmount = BigDecimal.valueOf((shoppingCart.getNumber() + 1) * setmeal.getPrice().doubleValue());
            } else if (dish != null) {
                newAmount = BigDecimal.valueOf((shoppingCart.getNumber() + 1) * dish.getPrice().doubleValue());
            }
            shoppingCart.setAmount(newAmount);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart, wrapper);
        } else {
            shoppingCart = new ShoppingCart();
            BigDecimal newAmount = BigDecimal.valueOf(0);
            if (setmeal != null) {
                BeanUtils.copyProperties(setmeal, shoppingCart);
                newAmount = BigDecimal.valueOf(setmeal.getPrice().doubleValue());
                shoppingCart.setSetmealId(setmeal.getId());
            } else if (dish != null) {
                BeanUtils.copyProperties(dish, shoppingCart);
                newAmount = BigDecimal.valueOf(dish.getPrice().doubleValue());
                shoppingCart.setDishId(dish.getId());
                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setAmount(newAmount);
            shoppingCart.setCreateTime(new Date());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    private ShoppingCart getShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .or()
                .eq(ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        return shoppingCartMapper.selectOne(queryWrapper);
    }

    private LambdaQueryWrapper<ShoppingCart> getQueryWrapper(ShoppingCartDTO shoppingCartDTO) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCartDTO.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId());
        queryWrapper.eq(shoppingCartDTO.getDishId() != null, ShoppingCart::getDishId, shoppingCartDTO.getDishId());
        return queryWrapper;
    }

    private void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .or()
                .eq(ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartMapper.delete(queryWrapper);
    }
}




