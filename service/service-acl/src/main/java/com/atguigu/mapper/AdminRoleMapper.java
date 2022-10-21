package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.AdminRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminRoleMapper extends BaseMapper<AdminRole> {
    /*
    根据id查询分配角色id列表
     */
    List<Long> findRoleIdListByAdminId(Long adminId);

    /**
     * 查询用户是否绑定过该角色
     * @param adminId
     * @param roleId
     * @return
     */
    AdminRole findByAdminIdAndRoleId(@Param("adminId") Long adminId,@Param("roleId") Long roleId);

    /**
     * 移除用户的角色
     * @param adminId
     * @param removeRoleIdList
     */
    void removeAdminRole(@Param("adminId") Long adminId,@Param("roleIds") List<Long> removeRoleIdList);

}
