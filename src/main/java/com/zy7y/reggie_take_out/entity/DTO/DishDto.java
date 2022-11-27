package com.zy7y.reggie_take_out.entity.DTO;

import com.zy7y.reggie_take_out.entity.DO.Dish;
import com.zy7y.reggie_take_out.entity.DO.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * dto 前端传过来
 * vo 后端反给前端
 * do 实体类，简单的可以直接用do 代替dto vo
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
