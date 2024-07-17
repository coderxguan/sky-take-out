package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

/**
 * 功能:
 * 作者:
 * 日期:2024/07/17 9:56
 */

public interface DishService {

    // 新增菜品
    public void saveWithFlavor(DishDTO dishDTO);

    // 菜品分页查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
}
