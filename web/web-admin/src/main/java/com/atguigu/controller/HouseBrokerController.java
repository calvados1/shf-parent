package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.base.BaseController;
import com.atguigu.entity.Admin;
import com.atguigu.entity.HouseBroker;
import com.atguigu.service.AdminService;
import com.atguigu.service.HouseBrokerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/houseBroker")
public class HouseBrokerController extends BaseController {
    @Reference
    private AdminService adminService;
    @Reference
    private HouseBrokerService houseBrokerService;
    private static final String PAGE_CREATE = "houseBroker/create";
    @GetMapping("/create")
    public String create(@RequestParam("houseId")Long houseId, Model model){
        //1. 查询出不是当前房源经纪人的admin列表,并存储到请求域
        List<Admin> adminList = adminService.findByHouseId(houseId);
        model.addAttribute("adminList",adminList);
        //2. 将houseId存储到请求域
        model.addAttribute("houseId",houseId);
        //3. 显示新增页面
        return PAGE_CREATE;
    }

    @PostMapping("/save")
    public String save(HouseBroker houseBroker,Model model) {
        Admin admin = adminService.getById(houseBroker.getBrokerId());
        houseBroker.setBrokerName(admin.getName());
        houseBroker.setBrokerHeadUrl(admin.getHeadUrl());
        houseBrokerService.insert(houseBroker);
        return successPage(model, "新增经纪人成功");
    }

    private final static String PAGE_EDIT = "houseBroker/edit";

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,Model model) {
        HouseBroker houseBroker = houseBrokerService.getById(id);
        List<Admin> adminList = adminService.findByHouseId(houseBroker.getHouseId());
        Admin admin = adminService.getById(houseBroker.getBrokerId());
        adminList.add(admin);
        model.addAttribute("adminList", adminList);
        model.addAttribute("houseBroker", houseBroker);
        return PAGE_EDIT;
    }

    @PostMapping("/update")
    public String update(HouseBroker houseBroker,Model model) {
        Admin admin = adminService.getById(houseBroker.getBrokerId());
        if (admin != null) {
            houseBroker.setBrokerName(admin.getName());
            houseBroker.setBrokerHeadUrl(admin.getHeadUrl());
        }
        houseBrokerService.update(houseBroker);
        return successPage(model, "修改经纪人信息成功");
    }

    private static final String SHOW_ACTION = "redirect:/house/";

    @RequestMapping("/delete/{houseId}/{id}")
    public String delete(@PathVariable Long id, @PathVariable Long houseId) {
        houseBrokerService.delete(id);
        return SHOW_ACTION + houseId;
    }
}