package com.atguigu.mapper;

import com.atguigu.entity.UserFollow;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

public interface UserFollowMapper {
    UserFollow findByUserIdAndHouseId(@Param("userId") Long userId, @Param("houseId") Long houseId);

    void update(UserFollow userFollow);

    void insert(UserFollow userFollow);

    Page<UserFollow> findListPage(@Param("userId") Long userId);
}
