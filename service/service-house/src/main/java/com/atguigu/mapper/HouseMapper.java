package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.House;
import com.atguigu.entity.vo.HouseQueryVo;
import com.atguigu.entity.vo.HouseVo;
import com.github.pagehelper.Page;

public interface HouseMapper extends BaseMapper<House> {
    Integer findCountByCommunityId(Long community);

    Page<HouseVo> findListPage(HouseQueryVo houseQueryVo);
}
