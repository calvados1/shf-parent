package com.atguigu.service;

import com.atguigu.base.BaseService;
import com.atguigu.entity.Role;

import java.util.List;
import java.util.Map;

public interface RoleService extends BaseService<Role> {
    List<Role> findAll();

    Map<String, List<Role>> findRoleByAdminId(Long adminId);

    /**
     * 保存用户角色
     * @param adminId
     * @param roleIdList
     */
    void saveAdminRole(Long adminId,List<Long> roleIdList);


}
