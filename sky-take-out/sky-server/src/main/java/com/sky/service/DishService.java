package com.sky.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishPageVO;
import com.sky.vo.DishVO;

import java.util.List;

/**
* @author tx
* @description 针对表【dish(菜品)】的数据库操作Service
* @createDate 2025-06-04 16:07:40
*/
public interface DishService extends IService<Dish> {

    void saveDishWithFlavors(DishDTO dishDTO);

    void updateDishWithFlavors(DishDTO dishDTO);

    IPage<DishPageVO> pageSelectWithCategoryName(IPage<DishPageVO> page, DishPageQueryDTO dishPageQueryDTO);

    List<DishVO> listWithFlavor(Long categoryId);
}
