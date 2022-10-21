package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.base.BaseController;
import com.atguigu.entity.Admin;
import com.atguigu.entity.Role;
import com.atguigu.service.AdminService;
import com.atguigu.service.RoleService;
import com.atguigu.util.FileUtil;
import com.atguigu.util.QiniuUtils;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    private final static String PAGE_INDEX = "admin/index";
    private final static String PAGE_CREATE = "admin/create";
    private final static String PAGE_EDIT = "admin/edit";
    private final static String LIST_ACTION = "redirect:/admin";

    @Reference
    private AdminService adminService;
    @Reference
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping
    public String index(@RequestParam Map<String, Object> filters, Model model) {
        PageInfo<Admin> pageInfo = adminService.findPage(filters);
        //System.out.println(filters); 这个filters是用来接收前端数据的，这里接收的是用来搜索的数据
        //在AdminMapper.xml中进行了模糊查询，
        //model.addAttribute用来向前端传数据
        model.addAttribute("page", pageInfo);
        model.addAttribute("filters", filters);
        return PAGE_INDEX;
    }

    @RequestMapping("/create")
    public String create() {
        return PAGE_CREATE;
    }

    @PostMapping("/save")
    public String save(Admin admin, Model model) {
        //1. 对admin的密码进行加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        //2. 调用业务层的方法保存用户信息
        adminService.insert(admin);
        //3. 显示成功页面
        return successPage(model, "添加用户信息成功");
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Admin admin = adminService.getById(id);
        model.addAttribute("admin", admin);
        return PAGE_EDIT;
    }

    @PostMapping("/update")
    public String update(Admin admin, Model model) {
        adminService.update(admin);
        return successPage(model, "编辑用户成功");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        adminService.delete(id);
        return LIST_ACTION;
    }

    private final static String PAGE_UPLOAD_SHOW = "admin/upload";

    @GetMapping("/uploadShow/{id}")
    public String uploadShow(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return PAGE_UPLOAD_SHOW;
    }

    @PostMapping("/upload/{id}")
    public String upload(@PathVariable("id") Long id, @RequestParam("file") MultipartFile multipartFile, Model model) throws IOException {
        //id是用户的id
        //1. 将图片上传到七牛云
        //生成一个唯一的文件名
        String originalFilename = multipartFile.getOriginalFilename();
        String uuidName = FileUtil.getUUIDName(originalFilename);
        QiniuUtils.upload2Qiniu(multipartFile.getBytes(), uuidName);

        //2. 将图片信息保存到数据库
        //2.1 获取图片的url
        String headUrl = QiniuUtils.getUrl(uuidName);
        //2.2 封装信息到Admin
        Admin admin = new Admin();
        admin.setId(id);
        admin.setHeadUrl(headUrl);
        //2.3 更新admin
        adminService.update(admin);

        //3. 显示成功页面
        return successPage(model, "上传头像成功");
    }

    private static final String PAGE_ASSIGN_SHOW = "admin/assignShow";

    @GetMapping("/assignShow/{id}")
    public String assignShow(@PathVariable Long id, Model model) {
        Map<String, List<Role>> roleListMap = roleService.findRoleByAdminId(id);
        model.addAttribute("adminId", id);
        model.addAllAttributes(roleListMap);
        return PAGE_ASSIGN_SHOW;

    }

    @PostMapping("/assignRole")
    public String assignRole(@RequestParam("adminId") Long adminId,
                             @RequestParam("roleIds") List<Long> roleIds,
                             Model model) {
        System.out.println(adminId);
        roleService.saveAdminRole(adminId, roleIds);
        return successPage(model, "保存角色成功");
    }


}
