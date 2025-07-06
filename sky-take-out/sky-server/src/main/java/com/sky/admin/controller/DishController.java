package com.sky.admin.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.anno.Log;
import com.sky.context.BaseContext;
import com.sky.exception.BaseException;
import com.sky.service.CategoryService;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.vo.DishPageVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {


    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
//    @Cacheable(cacheNames = CacheNames.ADMINDISH, key = "'pageSelect'")
    public Result<IPage<DishPageVO>> pageSelect(DishPageQueryDTO dishPageQueryDTO) {


        IPage<DishPageVO> page = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        IPage<DishPageVO> dishIPage = dishService.pageSelectWithCategoryName(page,dishPageQueryDTO);

        return Result.success(dishIPage);
    }

    /**
     * 根据分类查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId);
        List<Dish> dishList = dishService.list(queryWrapper);
        return Result.success(dishList);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> get(@PathVariable Long id) {
        DishVO dishVO = new DishVO();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId, id);
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setCategoryName(categoryService.getById(dish.getCategoryId()).getName());
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishVO.setFlavors(list);
        return Result.success(dishVO);
    }

    @PostMapping
    public Result<String> addDish(@RequestBody DishDTO dishDTO) {
        dishService.saveDishWithFlavors(dishDTO);
        return Result.success();
    }

    @DeleteMapping
    @Log
    @Transactional
    public Result<String> delete(@RequestParam List<Long> ids) {
        ids.forEach(id -> {
            List<SetmealDish> list = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().in(SetmealDish::getDishId, ids));
            if (!list.isEmpty()) {
                throw new BaseException("该菜品正在售卖中，不能删除");
            }
        });
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishService.removeBatchByIds(ids);
        dishFlavorService.remove(lambdaQueryWrapper);
        return Result.success();
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        dishService.updateDishWithFlavors(dishDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    @Log
    @Transactional
    public Result<String> updateStatus(@PathVariable Integer status, Long id) {
        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.eq(Dish::getId,id);
        dishLambdaUpdateWrapper.set(Dish::getStatus,status);
        dishService.update(new Dish(),dishLambdaUpdateWrapper);
//        int i =1/0;
        return Result.success();
    }
}


















