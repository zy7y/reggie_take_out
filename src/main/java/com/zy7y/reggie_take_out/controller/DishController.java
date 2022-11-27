package com.zy7y.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy7y.reggie_take_out.common.CustomException;
import com.zy7y.reggie_take_out.common.R;
import com.zy7y.reggie_take_out.entity.DO.Category;
import com.zy7y.reggie_take_out.entity.DO.Dish;
import com.zy7y.reggie_take_out.entity.DO.DishFlavor;
import com.zy7y.reggie_take_out.entity.DTO.DishDto;
import com.zy7y.reggie_take_out.service.CategoryService;
import com.zy7y.reggie_take_out.service.DishFlavorService;
import com.zy7y.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;


    @PostMapping
    public R<String > save(@RequestBody DishDto dishDto){
        dishService.saveDishWithFlavor(dishDto);
        return R.success("新增成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name){
        Page<Dish> dishPage = new Page<>(page, pageSize);

        // dish 模糊查询数据
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, queryWrapper);

        // 处理 dish 分页的records 加上category name
        List<Dish> dishList = dishPage.getRecords();

        List<DishDto> dishDtoList = dishList.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            // 将读取出来的dish对象中属性拷贝到dishDto中
            BeanUtils.copyProperties(dish, dishDto);
            // 设置&查询对应的categoryName
            Category category = categoryService.getById(dishDto.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());

        // 创建Dto 分页对象
        Page<DishDto> dishDtoPage = new Page<>();

        // 将dish page 对象 拷贝到 dishDto page 对象中并且不要实际的数据字段《records》, 除records外其他字段都有值
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 查商品详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDtoById(@PathVariable Long id){
        DishDto dishDto = dishService.getDishByIDWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateDishWithFlavor(dishDto);
        return R.success("更新成功");
    }

    /**
     * 更新售货状态
     * @param code
     * @param ids
     * @return
     */
    @PostMapping("/status/{code}")
    public R<String> updateStatus(@PathVariable Integer code, @RequestParam List<Long> ids){
        log.info("code {}", code);
        List<Dish> dishList = ids.stream().map(id -> {
            Dish dish = dishService.getById(id);
            dish.setStatus(code);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);
        return R.success("更新成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids)
                .eq(Dish::getStatus, 1);
        int count = dishService.count(queryWrapper);
        if (count > 0){
            throw new CustomException("菜品售卖中，无法删除");
        }
        dishService.removeByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 通过分类id 查询起售状态商品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = dishList.stream().map(item -> {
            Long dishId = item.getId(); // 菜品id, 查口味列表
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            DishDto dishDto = new DishDto(); // 设置dto
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setFlavors(dishFlavorService.list(lambdaQueryWrapper));
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
