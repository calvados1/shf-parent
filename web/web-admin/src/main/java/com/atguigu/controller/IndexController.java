package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Admin;
import com.atguigu.entity.Permission;
import com.atguigu.service.AdminService;
import com.atguigu.service.PermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {
    @Reference
    private AdminService adminService;
    @Reference
    private PermissionService permissionService;
    private static final String PAGE_INDEX = "frame/index";

    @GetMapping("/")
    public String index(Model model){
        //获取当前登录的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Admin admin = adminService.getByUsername(user.getUsername());
        //查询用户的权限列表 admin.getId()是long adminId
        List<Permission> permissionList = permissionService.findMenuPermissionByAdminId(admin.getId());
//        System.out.println(admin.getId());   1
        model.addAttribute("admin",admin);
        model.addAttribute("permissionList",permissionList);
        return PAGE_INDEX;
    }


    @RequestMapping("/main")
    public String main() {
        return "frame/main";
    }

    @RequestMapping("/dict")
    public String dict() {
        return "dict/index";
    }
}
