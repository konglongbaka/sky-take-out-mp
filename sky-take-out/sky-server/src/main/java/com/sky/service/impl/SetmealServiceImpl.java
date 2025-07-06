package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.mapper.DishMapper;
import com.sky.service.SetmealDishService;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author tx
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2025-06-04 16:07:40
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public List<Dish> listDishBySetmealId(Long id) {
        List<Long> ids = setmealMapper.getDishIdBySetmealId(id);
        return dishMapper.selectBatchIds(ids);
    }

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        setmealDTO.getSetmealDishes().forEach(item -> item.setSetmealId(setmeal.getId()));
        setmealDishService.saveBatch(setmealDTO.getSetmealDishes());
    }

    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateById(setmeal);
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmealDTO.getId()));
        setmealDTO.getSetmealDishes().forEach(item -> item.setSetmealId(setmeal.getId()));
        setmealDishService.saveBatch(setmealDTO.getSetmealDishes());
    }

    @Override
    public List<DishItemVO> listDishItemVOBySetmealId(Long id) {
        return setmealMapper.getDishItemVoBySetmealId(id);
    }
    }