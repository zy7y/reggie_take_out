package com.zy7y.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy7y.reggie_take_out.entity.DO.Dish;
import com.zy7y.reggie_take_out.entity.DTO.DishDto;

public interface DishService extends IService<Dish> {

    /**
     * 同时新增dish 和 dish——flavor
     * @param dishDto
     */
    void saveDishWithFlavor(DishDto dishDto);

    /**
     * 通过 dish id 查dish 数据 与 dish-flavor
     */
    DishDto getDishByIDWithFlavor(Long id);

    void updateDishWithFlavor(DishDto dishDto);
}
