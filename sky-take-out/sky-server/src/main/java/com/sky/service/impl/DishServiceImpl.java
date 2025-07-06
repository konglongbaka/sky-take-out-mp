package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.service.DishFlavorService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import com.sky.entity.DishFlavor;
import com.sky.vo.DishPageVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author tx
* @description 针对表【dish(菜品)】的数据库操作Service实现
* @createDate 2025-06-04 16:07:40
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    @Override
    @Transactional
    public void saveDishWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishService.save(dish);
        dishDTO.getFlavors().forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
        dishFlavorService.saveBatch(dishDTO.getFlavors());
    }

    @Override
    @Transactional
    public void updateDishWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishService.updateById(dish);
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishDTO.getId()));
        dishDTO.getFlavors().forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
        dishFlavorService.saveBatch(dishDTO.getFlavors());
    }

    @Override
    public IPage<DishPageVO> pageSelectWithCategoryName(IPage<DishPageVO> page, DishPageQueryDTO dishPageQueryDTO) {
        return dishMapper.pageSelectWithCategoryName(page, dishPageQueryDTO);
    }

    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        return dishMapper.listWithFlavor(categoryId);
    }
}




