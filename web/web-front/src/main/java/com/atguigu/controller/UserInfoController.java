package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.UserInfo;
import com.atguigu.entity.vo.LoginVo;
import com.atguigu.entity.vo.RegisterVo;
import com.atguigu.result.Result;
import com.atguigu.result.ResultCodeEnum;
import com.atguigu.service.UserInfoService;
import com.atguigu.util.MD5;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
    @Reference
    private UserInfoService userInfoService;

    @GetMapping("/sendCode/{phone}")
    public Result sendCode(@PathVariable("phone") String phone, HttpSession session) {
        //本应该调用阿里云短信(短信SDK)给phone发送短信
        //现在模拟一个短信
        String code = "1111";
        //实际开发中验证码应该是存储到Redis，并且设置时效性; 我们将其存储到session
        session.setAttribute("CODE", code);
        //验证码发送成功
        return Result.ok();
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterVo registerVo, HttpSession session) {
        //1. 校验验证码是否正确
        //1.1 获取session中保存的验证码
        String sessionCode = (String) session.getAttribute("CODE");
        //1.2 校验
        if (!registerVo.getCode().equalsIgnoreCase(sessionCode)) {
            //验证码校验失败
            return Result.build(null, ResultCodeEnum.CODE_ERROR);
        }
        //2. 校验手机号是否已存在
        //2.1 调用业务层的方法根据手机号查询用户信息
        UserInfo userInfo = userInfoService.getByPhone(registerVo.getPhone());
        //2.2 判断查询到的用户信息是否为null，如果不为null表示手机号已存在则不能注册
        if (userInfo != null) {
            //手机号已存在
            return Result.build(null, ResultCodeEnum.PHONE_REGISTER_ERROR);
        }
        //3. 对密码进行加密
        String encryptPassword = MD5.encrypt(registerVo.getPassword());
        //4. 调用业务层的方法将数据保存起来
        userInfo = new UserInfo();
        //拷贝属性
        BeanUtils.copyProperties(registerVo, userInfo);
        //重新设置密码
        userInfo.setPassword(encryptPassword);
        //设置status为1
        userInfo.setStatus(1);
        userInfoService.insert(userInfo);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo, HttpSession session) {
        //根据手机号查找用户，判断手机号是否正确
        UserInfo userInfo = userInfoService.getByPhone(loginVo.getPhone());
        if (userInfo == null) {
            //手机号错误
            return Result.build(null, ResultCodeEnum.ACCOUNT_ERROR);
        }
        //判断账号是否被锁定
        if (userInfo.getStatus() == 0) {
            return Result.build(null, ResultCodeEnum.ACCOUNT_LOCK_ERROR);
        }
        //判断密码是否正确
        if (!userInfo.getPassword().equals(MD5.encrypt(loginVo.getPassword()))) {
            //密码错误
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }
        //如果走到这里说明登录成功，将用户信息存储到session中
        session.setAttribute("USER", userInfo);

        //前端回显
        Map responseMapping = new HashMap();
        responseMapping.put("nickName", userInfo.getNickName());
        responseMapping.put("phone", userInfo.getPhone());
        return Result.ok(responseMapping);

    }

    @GetMapping("/logout")
    public Result logout(HttpSession session) {
        //从session中移除用户信息
        session.invalidate();
        return Result.ok();
    }

}