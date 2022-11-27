package com.zy7y.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy7y.reggie_take_out.entity.DO.Setmeal;
import com.zy7y.reggie_take_out.entity.DTO.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
