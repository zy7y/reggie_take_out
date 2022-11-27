package com.zy7y.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy7y.reggie_take_out.common.CustomException;
import com.zy7y.reggie_take_out.entity.DO.Setmeal;
import com.zy7y.reggie_take_out.entity.DO.SetmealDish;
import com.zy7y.reggie_take_out.entity.DTO.SetmealDto;
import com.zy7y.reggie_take_out.mapper.SetmealMapper;
import com.zy7y.reggie_take_out.service.SetmealDishService;
import com.zy7y.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，并像关联表中写入数据
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map(item -> {
                    item.setSetmealId(setmealDto.getId());
                    return item;
                }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 1. 查询状态能否删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);
        int count = this.count(lambdaQueryWrapper);
        if (count > 0){
            // 不能删除
            throw new CustomException("套餐售卖中，无法删除");
        }

        // 2. 可以删除
        this.removeByIds(ids);

        // 3. 删除关系表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);
    }
}
