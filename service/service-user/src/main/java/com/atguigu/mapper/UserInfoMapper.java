package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.UserInfo;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
    /*
     *根据用户手机号查询用户信息，如果手机号已存在则不能注册
     */
    UserInfo getByPhone(String phone);

}
