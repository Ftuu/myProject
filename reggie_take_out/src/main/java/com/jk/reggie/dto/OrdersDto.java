package com.jk.reggie.dto;

import com.jk.reggie.entity.OrderDetail;
import com.jk.reggie.entity.Orders;
import lombok.Data;

import java.util.List;
@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
}
