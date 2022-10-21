package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.Community;

import java.util.List;

public interface CommunityMapper extends BaseMapper<Community> {
    List<Community> findAll();
}
