package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    //新增套餐, 同时需要保存套餐和菜品的关联关系
    void saveWithDish(SetmealDTO setmealDTO);

    // 分页查询
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    // 批量删除套餐
    void deleteBatch(List<Long> ids);

    // 根据ID 查询套餐
    SetmealVO getByIdWithDish(Long id);

    // 修改套餐
    void update(SetmealDTO setmealDTO);

    // 套餐起售, 停售
    void startOrStop(Integer status, Long id);

    /*条件查询*/
    List<Setmeal> list(Setmeal setmeal);

    /* 根据id查询菜品选项*/
    List<DishItemVO> getDishItemById(Long id);
}
