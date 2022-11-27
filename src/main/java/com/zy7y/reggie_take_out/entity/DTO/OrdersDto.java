package com.zy7y.reggie_take_out.entity.DTO;

import com.zy7y.reggie_take_out.entity.DO.OrderDetail;
import com.zy7y.reggie_take_out.entity.DO.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
