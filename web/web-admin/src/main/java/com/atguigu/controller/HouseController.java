package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.base.BaseController;
import com.atguigu.en.DictCode;
import com.atguigu.en.HouseStatus;
import com.atguigu.entity.*;
import com.atguigu.service.*;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/house")
public class HouseController extends BaseController {
    private static final String PAGE_INDEX = "house/index";
    @Reference
    private HouseService houseService;

    @Reference
    private CommunityService communityService;

    @Reference
    private DictService dictService;

    @Reference
    private HouseImageService houseImageService;

    @Reference
    private HouseBrokerService houseBrokerService;

    @Reference
    private HouseUserService houseUserService;

    @RequestMapping
    public String index(@RequestParam Map<String, Object> filters, Model model) {
        //处理pageNum和pageSize为空的情况
        if (!filters.containsKey("pageNum")) {
            filters.put("pageNum", 1);
        }
        if (!filters.containsKey("pageSize")) {
            filters.put("pageSize", 10);
        }
        //1. 分页搜索房源列表信息
        PageInfo<House> pageInfo = houseService.findPage(filters);

        //2. 将房源分页信息存储到请求域
        model.addAttribute("page", pageInfo);

        //3. 将搜索条件存储到请求域
        model.addAttribute("filters", filters);

        //4. 查询所有小区、以及字典里的各种列表存储到请求域
        saveAllDictToRequestScope(model);

        return PAGE_INDEX;
    }

    /**
     * 查询所有字典信息以及小区列表并存储到请求域
     *
     * @param model
     */
    private void saveAllDictToRequestScope(Model model) {
        //1. 查询所有小区，并存储到请求域
        model.addAttribute("communityList", communityService.findAll());
        //2. 查询所有户型:根据父节点的dictCode查询子节点列表，并存储到请求域
        model.addAttribute("houseTypeList", dictService.findDictListByParentDictCode(DictCode.HOUSETYPE.code));
        //3. 查询所有楼层，并存储到请求域
        model.addAttribute("floorList", dictService.findDictListByParentDictCode(DictCode.FLOOR.code));
        //4. 查询所有建筑结构，并存储到请求域
        model.addAttribute("buildStructureList", dictService.findDictListByParentDictCode(DictCode.BUILDSTRUCTURE.code));
        //5. 查询所有朝向，并存储到请求域
        model.addAttribute("directionList", dictService.findDictListByParentDictCode(DictCode.DIRECTION.code));
        //6. 查询所有装修情况，并存储到请求域
        model.addAttribute("decorationList", dictService.findDictListByParentDictCode(DictCode.DECORATION.code));
        //7. 查询所有房屋用途，并存储到请求域
        model.addAttribute("houseUseList", dictService.findDictListByParentDictCode(DictCode.HOUSEUSE.code));
    }

    private static final String PAGE_CREATE = "house/create";

    @RequestMapping("/create")
    public String create(Model model) {
        saveAllDictToRequestScope(model);
        return PAGE_CREATE;
    }

    @PostMapping("/save")
    public String save(House house, Model model) {
        house.setStatus(HouseStatus.UNPUBLISHED.code);
        houseService.insert(house);
        return successPage(model, "添加房源信息成功");
    }

    private final static String PAGE_EDIT = "house/edit";

    //先进性回显，通过id查找,进行编辑
    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        House house = houseService.getById(id);
        model.addAttribute("house", house);
        saveAllDictToRequestScope(model);
        return PAGE_EDIT;
    }

    @PostMapping("/update")
    public String update(House house, Model model) {
        houseService.update(house);
        return successPage(model, "修改房源信息成功");
    }

    private static final String LIST_ACTION = "redirect:/house";
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        houseService.delete(id);
        return LIST_ACTION;
    }

    @GetMapping("/publish/{id}/{status}")
    public String publish(@PathVariable Long id, @PathVariable Integer status) {
        House house = new House();
        house.setId(id);
        house.setStatus(status);
        houseService.update(house);
        return LIST_ACTION;
    }

    private static final String PAGE_SHOW = "house/show";
    @GetMapping("/{houseId}")
    public String detail(@PathVariable("houseId") Long houseId,Model model){
        //1. 根据id查询房源详情
        House house = houseService.getById(houseId);
        //2. 根据小区id查询小区详情
        Community community = communityService.getById(house.getCommunityId());
        //3. 根据房源id查询房源的房源图片列表
        List<HouseImage> houseImage1List = houseImageService.findHouseImageList(houseId, 1);
        //4. 根据房源id查询房源的房产图片列表
        List<HouseImage> houseImage2List = houseImageService.findHouseImageList(houseId, 2);
        //5. 根据房源id查询房源的经纪人列表
        List<HouseBroker> houseBrokerList = houseBrokerService.findHouseBrokerListByHouseId(houseId);
        //6. 根据房源id查询房源的房东列表
        List<HouseUser> houseUserList = houseUserService.findHouseUserListByHouseId(houseId);

        //将上述查询到的内容存储到请求域
        model.addAttribute("house",house);
        model.addAttribute("community",community);
        model.addAttribute("houseImage1List",houseImage1List);
        model.addAttribute("houseImage2List",houseImage2List);
        model.addAttribute("houseBrokerList",houseBrokerList);
        model.addAttribute("houseUserList",houseUserList);

        return PAGE_SHOW;
    }

}