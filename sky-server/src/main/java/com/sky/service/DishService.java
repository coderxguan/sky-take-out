package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 菜品批量删除
    void deleteBatch(List<Long> ids);

    // 根据id查询菜品
    DishVO getByIdWithFlavor(Long id);

    // 修改菜品基本信息和口味信息
    void updateWithFlavor(DishDTO dishDTO);

    // 菜品起售停售
    void startOrStop(Integer status, Long id);

    // 根据分类ID查询菜品
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
