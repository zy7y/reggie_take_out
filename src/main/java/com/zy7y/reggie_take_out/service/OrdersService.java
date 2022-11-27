package com.zy7y.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy7y.reggie_take_out.entity.DO.Orders;

public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
