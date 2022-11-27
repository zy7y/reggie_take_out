package com.zy7y.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.zy7y.reggie_take_out.common.BaseContext;
import com.zy7y.reggie_take_out.common.R;
import com.zy7y.reggie_take_out.entity.DO.Orders;
import com.zy7y.reggie_take_out.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("提交成功");
    }

    /***
     * 移动端我的订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> list(Integer page, Integer pageSize){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId())
                .orderByDesc(Orders::getOrderTime);
        return R.success(ordersService.page(ordersPage, queryWrapper));
    }

    /**
     * 后台管理系统订单列表
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, Long number, String beginTime, String endTime){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null, Orders::getNumber, number)
                .between(beginTime != null && !beginTime.equals("") && endTime !=null && !endTime.equals("")
                        , Orders::getOrderTime, beginTime, endTime)
                .orderByDesc(Orders::getOrderTime);
        return R.success(ordersService.page(ordersPage, queryWrapper));

    }
}
