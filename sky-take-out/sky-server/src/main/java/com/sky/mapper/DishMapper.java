package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishPageVO;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author tx
 * @description 针对表【dish(菜品)】的数据库操作Mapper
 * @createDate 2025-06-04 16:07:40
 * @Entity com.sky.entity.Dish
 */
public interface DishMapper extends BaseMapper<Dish> {

    IPage<DishPageVO> pageSelectWithCategoryName(IPage<DishPageVO> page, DishPageQueryDTO dishPageQueryDTO);

    List<DishVO> listWithFlavor(Long categoryId);
}




