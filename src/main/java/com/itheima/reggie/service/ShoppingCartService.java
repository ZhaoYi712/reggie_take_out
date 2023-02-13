package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //添加到购物车
    R<ShoppingCart> add(ShoppingCart shoppingCart);

    //减少购物车数量
    ShoppingCart sub(ShoppingCart shoppingCart);


}
