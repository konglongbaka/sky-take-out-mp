package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

/**
* @author tx
* @description 针对表【shopping_cart(购物车)】的数据库操作Service
* @createDate 2025-06-04 16:07:40
*/
public interface ShoppingCartService extends IService<ShoppingCart> {

    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
