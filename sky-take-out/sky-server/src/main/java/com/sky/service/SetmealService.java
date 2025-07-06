package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;

import java.util.List;

/**
* @author tx
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2025-06-04 16:07:40
*/
public interface SetmealService extends IService<Setmeal> {

    List<Dish> listDishBySetmealId(Long id);

    void saveWithDish(SetmealDTO setmealDTO);

    void updateWithDish(SetmealDTO setmealDTO);

    List<DishItemVO> listDishItemVOBySetmealId(Long id);
}
