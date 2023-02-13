package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     */
    Page<SetmealDto> pages(int page , int pageSize , String name);

    /**
     * 单个或批量删除套餐
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 对套餐单个或批量进行停售或者是起售
     * @param status
     * @param ids
     */
    void status(Integer status , List<Long> ids);
}
