package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {


    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private UserService userService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private OrderDetailService orderDetailService;

    @Resource
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        // 1、获取当前用户id
        Long userId = BaseContext.getCurrentId();

        // 2、查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        // 查询用户数据
        User user = userService.getById(userId);
        // 查询地址数据
        Long addressBookId = orders.getAddressBookId(); //接收前端的地址id
        AddressBook addressBook = addressBookService.getById(addressBookId); //查询数据库地址簿id
        if (addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        // 生成订单号
        long orderId = IdWorker.getId();
        // 订单总金额，AtomicInteger用于对整形数据进行原子操作，保证整形数据的加减操作线程安全。
        AtomicInteger amount = new AtomicInteger(0);
        // 4、向订单明细表插入数据

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
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

        // 3、向订单表插入数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        // 3.1、向订单表插入数据，一条数据
        this.save(orders);

        // 4.1、向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        // 5、清空购物车数据
        shoppingCartService.remove(wrapper);
    }


    /**
     * 查看用户订单分页信息
     * @param page
     * @param pageSize
     */
    @Override
    public R<Page> userPage(int page , int pageSize) {

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
