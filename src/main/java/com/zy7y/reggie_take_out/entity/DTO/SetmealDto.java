package com.zy7y.reggie_take_out.entity.DTO;

import com.zy7y.reggie_take_out.entity.DO.Setmeal;
import com.zy7y.reggie_take_out.entity.DO.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
