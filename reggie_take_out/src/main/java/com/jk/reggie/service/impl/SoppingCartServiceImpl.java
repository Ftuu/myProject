package com.jk.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jk.reggie.entity.ShoppingCart;
import com.jk.reggie.mapper.ShoppingCartMapper;
import com.jk.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class SoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
