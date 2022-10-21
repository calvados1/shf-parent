package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.*;
import com.atguigu.entity.vo.HouseQueryVo;
import com.atguigu.entity.vo.HouseVo;
import com.atguigu.result.Result;
import com.atguigu.service.*;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/house")
public class HouseController {
    @Reference
    private HouseService houseService;
    @Reference
    private CommunityService communityService;
    @Reference
    private HouseBrokerService houseBrokerService;
    @Reference
    private UserFollowService userFollowService;
    @Reference
    private HouseImageService houseImageService;


    @PostMapping("/list/{pageNum}/{pageSize}")
    public Result findListPage(@RequestBody HouseQueryVo houseQueryVo,
                               @PathVariable("pageNum") Integer pageNum,
                               @PathVariable("pageSize") Integer pageSize){
        PageInfo<HouseVo> pageInfo = houseService.findListPage(pageNum, pageSize, houseQueryVo);
        return Result.ok(pageInfo);
    }



    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id, HttpSession session){
        //1. 查询客户端需要的数据，并且存储到Map对象中
        Map<String,Object> responseMap = new HashMap<>();
        //1.1 根据id查询房源信息,存储到responseMap中
        House house = houseService.getById(id);
        responseMap.put("house",house);
        //1.2 查询小区信息,存储到responseMap中
        Community community = communityService.getById(house.getCommunityId());
        responseMap.put("community",community);
        //1.3 查询房源的经纪人列表,存储到responseMap中
        List<HouseBroker> houseBrokerList = houseBrokerService.findHouseBrokerListByHouseId(id);
        responseMap.put("houseBrokerList",houseBrokerList);
        //1.4 查询房源的房源图片列表,存储到responseMap中
        List<HouseImage> houseImage1List = houseImageService.findHouseImageList(id, 1);
        responseMap.put("houseImage1List",houseImage1List);
        //1.5 查询用户是否已关注该房源,存储到responseMap中
        boolean isFollow = false;
        //1.5.1 从session中获取当前登录的用户信息
        UserInfo userInfo = (UserInfo) session.getAttribute("USER");
        //1.5.2 如果当前未登录，那么isFollow肯定是false
        if (userInfo != null) {
            //1.5.3 如果当前已登录，则判断当前用户是否已关注当前房源
            UserFollow userFollow = userFollowService.findByUserIdAndHouseId(userInfo.getId(), id);

            isFollow = (userFollow != null && userFollow.getIsDeleted() == 0);
        }

        responseMap.put("isFollow",isFollow);
        //2. 将响应数据封装到Result中，并返回
        return Result.ok(responseMap);
    }
}
