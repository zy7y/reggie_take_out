package com.zy7y.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy7y.reggie_take_out.common.CustomException;
import com.zy7y.reggie_take_out.entity.DO.Category;
import com.zy7y.reggie_take_out.entity.DO.Dish;
import com.zy7y.reggie_take_out.entity.DO.Setmeal;
import com.zy7y.reggie_take_out.mapper.CategoryMapper;
import com.zy7y.reggie_take_out.service.CategoryService;
import com.zy7y.reggie_take_out.service.DishService;
import com.zy7y.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;  // 菜品service

    @Autowired
    private SetmealService setmealService; // 套餐

    @Override
    public void removeById(Long id) {
        // 1. 是否关联菜品
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int dishResult = dishService.count(dishQueryWrapper);
        if (dishResult > 0){
            throw  new CustomException("请先删除关联菜品");
        }

        // 2. 是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealResult = setmealService.count(setmealQueryWrapper);
        if (setmealResult > 0) {
            throw  new CustomException("请先删除关联套餐");
        }
        // 调用父类根据id删除
        super.removeById(id);
    }
}
