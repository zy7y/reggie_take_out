package com.zy7y.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy7y.reggie_take_out.entity.DO.Category;

public interface CategoryService extends IService<Category> {

    void removeById(Long id);
}
