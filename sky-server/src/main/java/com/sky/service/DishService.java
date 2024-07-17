package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;

/**
 * 功能:
 * 作者:
 * 日期:2024/07/17 9:56
 */

public interface DishService {

    // 新增菜品
    public void saveWithFlavor(DishDTO dishDTO);
}
