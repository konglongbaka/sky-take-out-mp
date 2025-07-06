package com.sky.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;

import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetMealController")
@RequestMapping("/admin/setmeal")
public class SetMealController {

    @Autowired
    private SetmealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealDishService setmealDishService;
    @GetMapping("page")
//    @Cacheable(cacheNames = CacheNames.ADMINSETMEAL, key = "'pageSelect'")
    public Result<IPage<Setmeal>> page(SetmealPageQueryDTO setmealPageQueryDTO)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(setmealPageQueryDTO.getName()!=null, Setmeal::getName, setmealPageQueryDTO.getName());
        queryWrapper.eq(setmealPageQueryDTO.getCategoryId()!=null, Setmeal::getCategoryId, setmealPageQueryDTO.getCategoryId());
        queryWrapper.eq(setmealPageQueryDTO.getStatus()!=null, Setmeal::getStatus, setmealPageQueryDTO.getStatus());

        IPage<Setmeal> page = new Page<>(setmealPageQueryDTO.getPage(),  setmealPageQueryDTO.getPageSize());
        IPage<Setmeal> pageResult = setMealService.page(page,queryWrapper);
        return Result.success(pageResult);
    }


    @GetMapping("/{id}")
    public Result<SetmealVO> get(@PathVariable Long id) {
        SetmealVO setmealVO = new SetmealVO();
        Setmeal setMeal = setMealService.getById(id);
        BeanUtils.copyProperties(setMeal, setmealVO);
        setmealVO.setCategoryName(categoryService.getById(setMeal.getCategoryId()).getName());
        List<SetmealDish> list =setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
        setmealVO.setSetmealDishes(list);
        return Result.success(setmealVO);
    }

    @PostMapping
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setMealService.updateById(setmeal);
        return Result.success("修改成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO){
        setMealService.updateWithDish(setmealDTO);
        return Result.success("修改成功");
    }
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        setMealService.removeBatchByIds(ids);
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().in(SetmealDish::getSetmealId, ids));
        return Result.success("删除成功");
    }
}