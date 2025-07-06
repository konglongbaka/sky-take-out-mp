package com.sky.vo;

import com.sky.entity.Dish;
import lombok.Data;

@Data
public class DishPageVO extends Dish {
    private String categoryName;
}
