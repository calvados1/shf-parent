package com.atguigu.mapper;

import com.atguigu.entity.Dict;

import java.util.List;

public interface DictMapper {
    /*
     *根据父节点id查询子节点列表
     * @param parentId
     */
    List<Dict> findListByParentId(Long parentId);

    /*
     *判断是否是父节点
     */
    Integer countIsParent(Long parentId);

    /*
     *根据父节点dictCode查询子节点列表
     */
    List<Dict> findDictListByParentDictCode(String parentDictCode);
}
