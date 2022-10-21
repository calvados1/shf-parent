package com.atguigu.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.base.BaseMapper;
import com.atguigu.base.BaseServiceImpl;
import com.atguigu.entity.Permission;
import com.atguigu.entity.RolePermission;
import com.atguigu.helper.PermissionHelper;
import com.atguigu.mapper.PermissionMapper;
import com.atguigu.mapper.RolePermissionMapper;
import com.atguigu.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(interfaceClass = PermissionService.class)
public class PermissionServiceImpl extends BaseServiceImpl<Permission> implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    protected BaseMapper<Permission> getEntityMapper() {
        return permissionMapper;
    }

    @Override
    public List<Map<String, Object>> findPermissionByRoleId(Long id) {
        //查询所有权限
        List<Permission> permissionList = permissionMapper.findAll();
        List<Long> assignPermissionIdList = rolePermissionMapper.findPermissionIdListByRoleId(id);
        List<Map<String, Object>> zNodes = new ArrayList<>();

        if (!CollectionUtils.isEmpty(permissionList)) {
            for (Permission permission : permissionList) {
                //一个permission就对应一个Map<String,Object>
                Map<String, Object> map = new HashMap<>();
                //每一个map中要存储五个键值对:id、pid、name、open、checked
                //设置id,其实就是当前节点(permission)的id
                map.put("id", permission.getId());
                //设置pid,其实就是当前节点(permission)的parentId
                map.put("pId", permission.getParentId());
                //设置name,其实就是当前节点(permission)的name
                map.put("name", permission.getName());
                //设置open,只有父节点才有open属性,open表示是否展开
                //判断当前节点是否是父节点:以当前节点的id作为parentId查询子节点的数量，如果数量大于0，则表示当前节点是父节点
                if (permissionMapper.countIsParent(permission.getId()) > 0) {
                    //展开父节点
                    map.put("open", true);
                }
                //设置checked属性,表示当前节点是否选中:判断当前权限是否已经分配给了当前角色，如果已分配，则设置checked为true，否则不设置
                //判断当前权限的id是否在该角色已分配的权限id集合中
                if (!CollectionUtils.isEmpty(assignPermissionIdList) && assignPermissionIdList.contains(permission.getId())) {
                    //说明该权限是已分配的权限，则设置checked为true
                    map.put("checked", true);
                }
                //将map添加到zNodes集合中
                zNodes.add(map);
            }
        }
        return zNodes;
    }

    @Override
    public void saveRolePermission(Long roleId, List<Long> permissionIdList) {
        //查询当前角色的所有permissionId
        List<Long> rolePermissionIdList = rolePermissionMapper.findPermissionIdListByRoleId(roleId);
        //找出要移除的permissionId
        List<Long> removePermissionIdList = rolePermissionIdList.stream()
                .filter(item -> !permissionIdList.contains(item))
                .collect(Collectors.toList());
        //删除角色权限
        if (removePermissionIdList != null && removePermissionIdList.size() > 0) {
            rolePermissionMapper.removeRolePermission(roleId, removePermissionIdList);
        }

        //给角色添加权限
        for (Long permissionId : permissionIdList) {
            //根据roleId和permissionId查询角色权限信息
            RolePermission rolePermission = rolePermissionMapper.findByRoleIdAndPermissionId(roleId, permissionId);
            //判断当前roleId和permissionId是否存在关联
            if (rolePermission == null) {
                rolePermission = new RolePermission();
                rolePermission.setPermissionId(permissionId);
                rolePermission.setRoleId(roleId);
                rolePermissionMapper.insert(rolePermission);
            } else {
                if (rolePermission.getIsDeleted() == 1) {
                    rolePermission.setIsDeleted(0);
                    rolePermissionMapper.update(rolePermission);
                }
            }
        }
    }

    @Override
    public List<Permission> findMenuPermissionByAdminId(Long adminId) {
        List<Permission> permissionList = null;
        //判断是否是超级管理员
        if (adminId == 1) {
            //超级管理员
            permissionList = permissionMapper.findAll();
        } else {
            //根据adminId查询权限列表
            permissionList = permissionMapper.findPermissionListByAdminId(adminId);
        }
        //构建树形菜单
        return PermissionHelper.build(permissionList);
    }

    @Override
    public List<Permission> findAllMenu() {
        List<Permission> permissionList = permissionMapper.findAll();
        return PermissionHelper.build(permissionList);
    }

    @Override
    public List<String> findCodePermissionListByAdminId(Long adminId) {
        //判断是否是超级管理员
        if (adminId == 1) {
            //拥有所有权限
            return permissionMapper.findAllCodePermission();
        }
        return permissionMapper.findCodePermissionListByAdminId(adminId);
    }

    @Override
    public void insert(Permission permission) {
        Permission dbPermission = permissionMapper.findByParentIdAndName(permission.getParentId(), permission.getName());
        if (dbPermission != null) {
            throw new RuntimeException("菜单不存在");
        }
        super.insert(permission);
    }

    @Override
    public void delete(Long id) {
        Integer count = permissionMapper.findCountByParentId(id);
        if (count > 0) {
            throw new RuntimeException("菜单里有子菜单不能删除");
        }
        super.delete(id);
    }

    @Override
    public void update(Permission permission) {
        Permission originPermission = permissionMapper.getById(permission.getId());
        //根据要修改的这个权限的parentId和要修改成为的name进行查找
        Permission dbPermission = permissionMapper.findByParentIdAndName(originPermission.getParentId(), permission.getName());
        if (dbPermission != null && !dbPermission.equals(originPermission.getId())) {
            throw new RuntimeException("menu already exist");
        }
        super.update(permission);
    }
}
