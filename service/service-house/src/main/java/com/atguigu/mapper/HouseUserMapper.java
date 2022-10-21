package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.HouseUser;

import java.util.List;

public interface HouseUserMapper extends BaseMapper<HouseUser> {
    List<HouseUser> findHouseUserListByHouseId(Long houseId);
}
