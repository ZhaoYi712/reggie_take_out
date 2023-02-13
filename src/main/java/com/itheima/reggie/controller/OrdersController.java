package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);

        return R.success("下单成功");
    }


    /**
     * 用户订单分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userOrder(int page, int pageSize){

        // 1、构建分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        // 2、构建查询条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 根据订单更新时间排序
        queryWrapper.orderByDesc(Orders::getOrderTime);

        // 3、执行分页查询
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }
}
