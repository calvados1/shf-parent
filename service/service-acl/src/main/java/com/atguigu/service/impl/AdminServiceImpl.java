package com.atguigu.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.base.BaseMapper;
import com.atguigu.base.BaseServiceImpl;
import com.atguigu.entity.Admin;
import com.atguigu.mapper.AdminMapper;
import com.atguigu.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*在Dubbo的@Service注解类中，要想实现事务管理，
 *必须在@Service后面加上(interfaceClass = “实现的接口.class”)
 */
@Service(interfaceClass = AdminService.class)
@Transactional(propagation = Propagation.REQUIRED)
public class AdminServiceImpl extends BaseServiceImpl<Admin> implements AdminService {
    //自动装配用于调用方法
    @Autowired
    private AdminMapper adminMapper;

    @Override
    protected BaseMapper<Admin> getEntityMapper() {
        return adminMapper;
    }

    @Override
    public void insert(Admin admin) {
        Admin dbUsernameAdmin = adminMapper.getByUsername(admin.getUsername());
        if (dbUsernameAdmin != null) {
            throw new RuntimeException("yonghucunzai");
        }
        Admin dbPhoneAdmin = adminMapper.getByPhone(admin.getPhone());
        if (dbPhoneAdmin != null) {
            throw new RuntimeException("shoujihaocunzai");
        }
        super.insert(admin);
    }

    @Override
    public void update(Admin admin) {
        Admin dbPhoneUser = adminMapper.getByPhoneAndId(admin.getPhone(), admin.getId());
        if (dbPhoneUser != null) {
            throw new RuntimeException("手机号已存在");
        }
        super.update(admin);
    }

    @Override
    public List<Admin> findByHouseId(Long houseId) {
        return adminMapper.findByHouseId(houseId);
    }

    @Override
    public Admin getByUsername(String username) {
        return adminMapper.getByUsername(username);
    }
}