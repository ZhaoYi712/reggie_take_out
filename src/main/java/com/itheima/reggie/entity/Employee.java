package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
* 员工实体
*/
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)  //修复java的long型数据与前端js消息兼容
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;  //身份证号码

    private Integer status;

    @TableField(fill = FieldFill.INSERT)    // 插入时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)    // 插入时填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时填充字段
    private Long updateUser;

}
