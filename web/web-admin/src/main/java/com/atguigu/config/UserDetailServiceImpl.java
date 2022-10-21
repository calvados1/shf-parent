package com.atguigu.config;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Admin;
import com.atguigu.service.AdminService;
import com.atguigu.service.PermissionService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDetailServiceImpl implements UserDetailsService {
    @Reference
    private AdminService adminService;
    @Reference
    protected PermissionService permissionService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminService.getByUsername(username);
        if(null == admin) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        //获取用户权限列表
        List<String> codePermissionList = permissionService.findCodePermissionListByAdminId(admin.getId());

        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        for (String code : codePermissionList) {
            if(StringUtils.isEmpty(code)) {
                continue;
            }
            grantedAuthorityList.add(new SimpleGrantedAuthority(code));
        }
        return new User(username,admin.getPassword(), grantedAuthorityList);
    }
}