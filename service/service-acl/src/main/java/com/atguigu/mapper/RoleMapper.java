package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    List<Role> findAll();

    Role getByRoleName(String roleName);

/*    Role getById(Long id);

    void insert(Role role);

    void update(Role role);

    *//*Role getByRoleNameAndId(@Param("roleName") String roleName, @Param("id") Long id);*//*

    void delete(Long id);

    Page<Role> findPage(Map<String, Object> filters);*/
}
