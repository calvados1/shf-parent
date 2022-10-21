package com.atguigu.mapper;

import com.atguigu.base.BaseMapper;
import com.atguigu.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    /*
     * 查询所有权限
     */
    List<Permission> findAll();

    Integer countIsParent(Long id);

    List<Permission> findPermissionListByAdminId(Long adminId);

    Permission findByParentIdAndName(@Param("parentId") Long parentId, @Param("name") String name);

    Integer findCountByParentId(Long parentId);

    List<String> findCodePermissionListByAdminId(Long adminId);

    List<String> findAllCodePermission();
}
