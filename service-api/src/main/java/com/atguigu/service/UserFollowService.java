package com.atguigu.service;

import com.atguigu.entity.UserFollow;
import com.atguigu.entity.vo.UserFollowVo;
import com.github.pagehelper.PageInfo;

public interface UserFollowService {
    UserFollow findByUserIdAndHouseId(Long userId, Long houseId);

    void update(UserFollow userFollow);

    void insert(UserFollow userFollow);

    /**
     * 分页查询用户关注列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    PageInfo<UserFollowVo> findListPage(int pageNum, int pageSize, Long userId);

}
