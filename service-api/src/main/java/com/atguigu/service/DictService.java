package com.atguigu.service;

import com.atguigu.entity.Dict;

import java.util.List;
import java.util.Map;

public interface DictService {
    List<Map<String, Object>> findZnodes(Long id);

    /*
     *根据dictCode查询dict集合
     */
    List<Dict> findDictListByParentDictCode(String parentDictCode);

    /*
     *根据父节点id查询所有子节点
     */
    List<Dict> findDictListByParentId(Long parentId);
}
