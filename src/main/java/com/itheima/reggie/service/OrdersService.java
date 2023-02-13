package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    //用户下单
    void submit(Orders orders);

    //查看订单分页
    R<Page> userPage(int page , int pageSize);
}
