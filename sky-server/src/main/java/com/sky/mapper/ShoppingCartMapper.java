package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    // 动态条件查询
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    // 根据id修改商品数量
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);


    // 插入购物车数据
    @Insert("insert into shopping_cart(name, user_id, dish_id, setmeal_id, dish_flavor, amount, image, create_time) " +
            "VALUES (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{image},#{createTime}) ")
    void insert(ShoppingCart shoppingCart);

    // 清空购物车
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    // 根据id删除购物车数据
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    // 批量插入购物车数据
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
