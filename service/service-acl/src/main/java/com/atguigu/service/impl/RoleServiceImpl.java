package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.base.BaseMapper;
import com.atguigu.base.BaseServiceImpl;
import com.atguigu.entity.AdminRole;
import com.atguigu.entity.Role;
import com.atguigu.mapper.AdminRoleMapper;
import com.atguigu.mapper.RoleMapper;
import com.atguigu.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = RoleService.class)
@Transactional(propagation = Propagation.REQUIRED)
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;

    @Override
    protected BaseMapper<Role> getEntityMapper() {
        return roleMapper;
    }

    @Transactional(rollbackFor = RuntimeException.class,
            readOnly = true,
            propagation = Propagation.SUPPORTS)
    @Override
    public List<Role> findAll() {
        //查询角色全部信息
        return roleMapper.findAll();
    }

    @Override
    public Map<String, List<Role>> findRoleByAdminId(Long adminId) {
        //查询role所有字段
        List<Role> allRoleList = roleMapper.findAll();

        List<Long> assignRoleIdList = adminRoleMapper.findRoleIdListByAdminId(adminId);
        List<Role> unAssignRoleList = new ArrayList<>();
        List<Role> assignRoleList = new ArrayList<>();
        for (Role role : allRoleList) {
            if (assignRoleIdList.contains(role.getId())) {
                assignRoleList.add(role);
            } else {
                unAssignRoleList.add(role);
            }
        }
        Map<String, List<Role>> roleMap = new HashMap<>();
        roleMap.put("unAssignRoleList", unAssignRoleList);
        roleMap.put("assignRoleList", assignRoleList);
        return roleMap;
    }

    //复杂方法:代码复杂，但是可以避免产生重复冗余数据
    @Override
    public void saveAdminRole(Long adminId, List<Long> roleIds) {
        //1. 找出需要取消绑定的那些角色id
        //1.1 找出这次保存之前该用户已绑定的所有角色id(原本在右边的那些角色id)

        //找出右面已经分配了的全部角色
        List<Long> originalAssignRoleIds = adminRoleMapper.findRoleIdListByAdminId(adminId);
        //1.2 roleIds表示这次保存需要给该用户绑定的所有角色id(这次在右边的那些角色id)
        //1.3 要取消绑定的就是(原本已绑定的角色id集合与这次需要绑定的角色id集合的差集)
        //1.3.1 声明一个新的集合用来存储要取消绑定的角色的id
        List<Long> removeRoleIds = new ArrayList<>();
        //1.3.2 遍历出该用户原来已绑定的每个角色id

        //CollectionUtils.isEmpty判断是否为空，为空返回true，前面加上！
        // （）里面条件变为不为空为true，说明roleIds不为空
        if (!CollectionUtils.isEmpty(roleIds)) {
            //遍历右侧里面所有的id
            for (Long roleId : originalAssignRoleIds) {
                //1.3.3 如果roleId没有在这次需要绑定的id集合roleIds中，那么就表示需要取消绑定

                //contains判断元素是否存在，存在则为true
                //此行代码为如果不存在则添加进取消绑定的集合
                if (!roleIds.contains(roleId)) {
                    removeRoleIds.add(roleId);
                }
            }
        } else {
            //如果roleIds为空，表示这次不需要绑定任何角色(要删除原来已绑定的所有角色)
            removeRoleIds = originalAssignRoleIds;
        }

        //2. 调用持久层的方法取消绑定
        if (!CollectionUtils.isEmpty(removeRoleIds)) {
            adminRoleMapper.removeAdminRole(adminId,removeRoleIds);
        }

        //3. 需要绑定给当前用户的角色(roleIds就是需要绑定给当前用户的所有角色id)，进行判断
        if (!CollectionUtils.isEmpty(roleIds)) {
            //3.1 遍历出要绑定的每一个角色id
            for (Long roleId : roleIds) {
                //3.2 查询当前角色id以前是否绑定给过当前用户(不用考虑isDeleted)
                AdminRole adminRole = adminRoleMapper.findByAdminIdAndRoleId(adminId,roleId);
                if (adminRole != null) {
                    if (adminRole.getIsDeleted() == 1) {
                        //3.2.1 之前已经绑定过，但是取消绑定了，设置is_deleted=0,修改数据
                        adminRole.setIsDeleted(0);
                        adminRoleMapper.update(adminRole);
                    }
                    //3.2.2 之前已经绑定过，并且没有取消绑定，不需要动
                } else {
                    //3.2.3 之前从未绑定过，则新增数据
                    adminRole = new AdminRole();
                    adminRole.setAdminId(adminId);
                    adminRole.setRoleId(roleId);
                    adminRoleMapper.insert(adminRole);
                }
            }
        }
    }
    @Override
    public void insert(Role role) {
        if (roleMapper.getByRoleName(role.getRoleName()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        super.insert(role);
    }


}
