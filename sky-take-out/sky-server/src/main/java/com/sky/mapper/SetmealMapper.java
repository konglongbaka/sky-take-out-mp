package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.vo.DishItemVO;

import java.util.List;

/**
* @author tx
* @description 针对表【setmeal(套餐)】的数据库操作Mapper
* @createDate 2025-06-04 16:07:40
* @Entity com.sky.entity.Setmeal
*/
public interface SetmealMapper extends BaseMapper<Setmeal> {


    List<Long> getDishIdBySetmealId(Long id);

    List<DishItemVO> getDishItemVoBySetmealId(Long id);
}




