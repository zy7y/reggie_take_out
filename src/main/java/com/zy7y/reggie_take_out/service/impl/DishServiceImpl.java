package com.zy7y.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy7y.reggie_take_out.entity.DO.Dish;
import com.zy7y.reggie_take_out.entity.DTO.DishDto;
import com.zy7y.reggie_take_out.entity.DO.DishFlavor;
import com.zy7y.reggie_take_out.mapper.DishMapper;
import com.zy7y.reggie_take_out.service.DishFlavorService;
import com.zy7y.reggie_take_out.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    @Transactional // 事务
    public void saveDishWithFlavor(DishDto dishDto) {
        // 新增数据到dish表， 拿到dishId
     this.save(dishDto);
     Long dishId = dishDto.getId();

     List<DishFlavor> dishFlavorList = dishDto.getFlavors();

     dishFlavorList = dishFlavorList.stream().map(item -> {
         item.setDishId(dishId);
         return item;
     }).collect(Collectors.toList());

     dishFlavorService.saveBatch(dishFlavorList);

    }

    @Override
    public DishDto getDishByIDWithFlavor(Long id) {
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);

        // copy
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        // 1, 更新 dish
        this.updateById(dishDto);

        // 清理 dish flavor 关联数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map(item ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
