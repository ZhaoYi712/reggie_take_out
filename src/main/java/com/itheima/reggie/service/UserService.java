package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {

    //发送手机短信验证码
    R<User> sendMsg(User user, HttpSession session);

    //移动端用户登录
    R<User> login(Map map , HttpSession session);
}
