package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingCart) {
        // 1、设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId(); //获取当前线程的id
        shoppingCart.setUserId(currentId);  //将当前id插入购物车模型数据

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId); //购物车用户=当前线程用户

        if (dishId != null){
            // 添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前菜品或套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);


        if (cartServiceOne != null){
            // 3、如果已经存在，就在原来的数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            // 4、如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);  //数据库设计时已默认为1，这行可忽略
            shoppingCartService.save(shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
        }
        return R.success(cartServiceOne);
    }


    /**
     * 减少购物车数量
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {
        // 获取传入的菜品参数
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId()); //数据库用户id == 当前线程的id

        // 2、判断当前要删除的是菜品还是套餐
        if (dishId != null){
            //要删除的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());  //数据库菜品id == 前端菜品id
        }else {
            //要删除的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());  //数据库套餐id == 前端套餐id
        }
        // 查询购物车数据
        ShoppingCart shoppingCartData = shoppingCartService.getOne(queryWrapper);
        // 获取购物车数量
        Integer number = shoppingCartData.getNumber();
        // 判断购物车数量
        if (number > 0){
            Integer newNumber = shoppingCartData.getNumber() - 1;  //购物车数量-1
            shoppingCartData.setNumber(newNumber);
            shoppingCartService.updateById(shoppingCartData);  //将新数据插入数据库
        }
        //获取购物车最新数量
        Integer number2 = shoppingCartData.getNumber();
        if (number2 == 0){
            //如果购物车的数量减为0，那么就从购物车中删除
            Long cartDataId = shoppingCartData.getId();  //获取当前删除数据的id号
            shoppingCartService.removeById(cartDataId);  //根据id删除对应菜品或套餐
        }
        return shoppingCartData;
    }


}
