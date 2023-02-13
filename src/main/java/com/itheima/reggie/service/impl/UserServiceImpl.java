package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserService userService;


    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @Override
    public R<User> sendMsg(User user , HttpSession session) {
        // 1.获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            // 2.生成随机的4位验证码
            String  code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code：{}",code);

            // 3.调用阿里云提供的短信服务API完成短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            // 4.将生成的验证码保存到session中
            session.setAttribute(phone,code);

            return R.success(user);
        }
        return R.error("验证码发送失败");
    }


    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @Override
    public R<User> login(Map map , HttpSession session) {
        // 1、获取手机号
        String phone = map.get("phone").toString();

        // 2、获取验证码
        String code = map.get("code").toString();

        // 3、从session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);  //phone在map中是键，所以获取该键的值code

        // 4、进行验证码比对，（页面提交的验证码与session中的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)){
            // 4、如果对比成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
                // 5、判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
