package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.base.BaseController;
import com.atguigu.entity.Permission;
import com.atguigu.service.PermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/permission")
public class PermissionController extends BaseController {
    private final static String PAGE_INDEX = "permission/index";
    @Reference
    private PermissionService permissionService;

    @GetMapping
    public String index(Model model) {
        List<Permission> permissionList = permissionService.findAllMenu();
        model.addAttribute("list", permissionList);
        return PAGE_INDEX;
    }

    private static final String PAGE_CREATE = "permission/create";

    @GetMapping("/create")
    public String create(Permission permission, Model model) {
        model.addAttribute("permission", permission);
        return PAGE_CREATE;
    }

    @PostMapping("/save")
    public String save(Permission permission, Model model) {
        permissionService.insert(permission);
        return successPage(model, "菜单添加成功");
    }

    private static final String LIST_ACTION = "redirect:/permission";

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        permissionService.delete(id);
        return LIST_ACTION;
    }

    private static final String PAGE_EDIT = "permission/edit";

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Permission permission = permissionService.getById(id);
        model.addAttribute("permission", permission);
        return PAGE_EDIT;
    }
    @PostMapping("/update")
    public String update(Permission permission,Model model){
        permissionService.update(permission);
        return successPage(model,"修改菜单成功");
    }
}
