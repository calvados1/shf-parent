package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.Admin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminMapper extends BaseMapper<Admin> {
    Admin getByUsername(String username);

    Admin getByPhone(String phone);

    Admin getByPhoneAndId(@Param("phone") String phone, @Param("id") Long id);

    List<Admin> findByHouseId(Long houseId);

}
