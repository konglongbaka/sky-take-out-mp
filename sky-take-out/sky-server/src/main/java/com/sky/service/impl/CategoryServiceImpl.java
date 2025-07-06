package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import org.springframework.stereotype.Service;

/**
* @author tx
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2025-06-04 16:07:40
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService {

}




