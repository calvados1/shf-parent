package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.base.BaseController;
import com.atguigu.entity.Role;
import com.atguigu.service.PermissionService;
import com.atguigu.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {
    @Reference
    private RoleService roleService;
    @Reference
    private PermissionService permissionService;
    private final static String LIST_ACTION = "redirect:/role";
    private static final String PAGE_ASSIGN_SHOW = "role/assignShow";

    @PreAuthorize("hasAnyAuthority('role.show')")
    @RequestMapping
    public String index(@RequestParam Map conditions, Model model){
        if(!conditions.containsKey("pageNum")) {
            conditions.put("pageNum", 1);
        }
        if(!conditions.containsKey("pageSize")) {
            conditions.put("pageSize", 10);
        }

        PageInfo<Role> pageInfo = roleService.findPage(conditions);
        model.addAttribute("page", pageInfo);
        model.addAttribute("conditions", conditions);
        return "role/index";
    }


    //这个应该是用了mvc
    @RequestMapping("/create")
    public String create() {
        return "role/create";
    }

    @PreAuthorize("hasAnyAuthority('role.create')")
    @PostMapping("/save")
    public String saveRole(Role role, Model model){
        roleService.insert(role);
        return successPage(model,"添加角色成功");
    }


    @PreAuthorize("hasAnyAuthority('role.show')")
    @GetMapping("/edit/{id}")
    public String findRoleById(@PathVariable("id") Long id,Model model){
        Role role = roleService.getById(id);
        model.addAttribute("role",role);
        return "role/edit";
    }

    @PreAuthorize("hasAnyAuthority('role.edit')")
    @PostMapping("/update")
    public String updateRole(Role role, Model model){
        roleService.update(role);
        return successPage(model,"更新角色成功");
    }

    @PreAuthorize("hasAnyAuthority('role.delete')")
    @GetMapping("/delete/{id}")
    public String deleteRoleById(@PathVariable("id") Long id){
        roleService.delete(id);
        return LIST_ACTION;
    }

    @PreAuthorize("hasAnyAuthority('role.assign')")
    @GetMapping("/assignShow/{roleId}")
    public String assignShow(@PathVariable("roleId") Long roleId,Model model){
        List<Map<String, Object>> zNodes = permissionService.findPermissionByRoleId(roleId);
        model.addAttribute("zNodes", JSON.toJSONString(zNodes));
        model.addAttribute("roleId",roleId);
        return PAGE_ASSIGN_SHOW;
    }

    @PreAuthorize("hasAnyAuthority('role.assign')")
    @PostMapping("/assignPermission")
    public String assignPermission(@RequestParam("roleId") Long roleId,
                                   @RequestParam("permissionIds") List<Long> permissionIds,
                                   Model model){
        permissionService.saveRolePermission(roleId,permissionIds);
        return successPage(model,"设置角色权限成功");
    }
}