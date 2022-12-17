package com.jk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jk.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
