package com.sky.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.service.CategoryService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@Slf4j
@RequestMapping("/admin/category")
//400代表没有接受到参数
//500代表程序报错
public class CategoryController {
    public static final String CATEGORY_KEY = "category";
    @Autowired
    private CategoryService categoryService;

    private StringRedisTemplate redisTemplate;
    /**
     * 分页查询菜品分类
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
//    @Cacheable(cacheNames = "ADMIN CATEGORY", key = "'pageSelect'")
    public Result pageSelectCategory(CategoryPageQueryDTO categoryPageQueryDTO) {




        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryPageQueryDTO.getType()!=null, Category::getType, categoryPageQueryDTO.getType())
                    .like(categoryPageQueryDTO.getName()!=null, Category::getName, categoryPageQueryDTO.getName())
                    .orderByDesc(Category::getStatus)
                    .orderByAsc(Category::getType)
                    .orderByAsc(Category::getSort);
        IPage<Category> pageResult = categoryService.page(new Page<>(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize()),queryWrapper);
        return Result.success(pageResult);
    }


    /**
     * 新增菜品分类
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryService.save(category);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Category>> selectByCategoryType(Integer type) {
        log.info("查询分类：{}", type);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType, type);
        List<Category> result = categoryService.list(queryWrapper);
        return Result.success(result);
    }

    /**
     * 更改商品状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateCategoryStatus(@PathVariable Integer status, Long id) {
        log.info("修改商品状态：{},{}", status, id);
//        LambdaUpdateWrapper<Category> queryWrapper = new LambdaUpdateWrapper<>();
//        queryWrapper.eq(Category::getId, id);
//        queryWrapper.set(Category::getStatus, status);
//        categoryService.update(queryWrapper);
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        categoryService.saveOrUpdate(category);
        return Result.success();
    }
    /**
     * 修改菜品分类
     *
     * @param categoryDTO
     * @return
     */
    @PutMapping
    public Result<String> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改菜品分类：{}", categoryDTO);
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryService.saveOrUpdate(category);
        return Result.success();
    }

    /**
     * 删除菜品分类
     *
     * @param id
     * @return
     **/
    @DeleteMapping
    public Result<String> deleteCategory(Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}