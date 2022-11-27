package com.zy7y.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy7y.reggie_take_out.entity.DO.OrderDetail;
import com.zy7y.reggie_take_out.mapper.OrderDetailMapper;
import com.zy7y.reggie_take_out.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
