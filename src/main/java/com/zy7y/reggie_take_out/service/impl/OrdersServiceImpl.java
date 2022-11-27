package com.zy7y.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy7y.reggie_take_out.common.BaseContext;
import com.zy7y.reggie_take_out.common.CustomException;
import com.zy7y.reggie_take_out.entity.DO.*;
import com.zy7y.reggie_take_out.mapper.OrdersMapper;
import com.zy7y.reggie_take_out.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 提交订单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 0. 查购物车
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("购物车无数据");
        }
        // 1. 查用户
        User user = userService.getById(BaseContext.getCurrentId());

        // 2. 查地址
        Long addressId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressId);
        if (addressBook == null){
            throw new CustomException("收货地址不存在");
        }

        Long id = IdWorker.getId(); // 订单id

        AtomicInteger amount = new AtomicInteger(0); // 总金额
        List<OrderDetail> orderDetails = shoppingCartList.stream().map(item ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        // 3. 订单表写数据
        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(user.getId());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(
                (addressBook.getProvinceName() == null ? "": addressBook.getProvinceName())
                + (addressBook.getDistrictName() == null ? "": addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "": addressBook.getDetail())
        );

        this.save(orders);

        // 4 订单详细表写数据
        orderDetailService.saveBatch(orderDetails);

        // 5 清空购物车
        shoppingCartService.remove(queryWrapper);


    }
}
