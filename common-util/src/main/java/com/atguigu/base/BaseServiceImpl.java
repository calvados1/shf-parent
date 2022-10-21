package com.atguigu.base;

import com.atguigu.util.CastUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public abstract class BaseServiceImpl<T> {

    //抽象方法，其他类重写此方法用于调用方法
    protected abstract BaseMapper<T> getEntityMapper();

    public void insert(T t) {
        getEntityMapper().insert(t);
    }

    public void delete(Long id) {
        getEntityMapper().delete(id);
    }

    public void update(T t) {
        getEntityMapper().update(t);
    }

   @Transactional(propagation = Propagation.SUPPORTS)
    public T getById(Long id) {
        return getEntityMapper().getById(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PageInfo<T> findPage(Map<String, Object> filters) {
        int pageNum = CastUtil.castInt(filters.get("pageNum"), 1);
        int pageSize = CastUtil.castInt(filters.get("pageSize"), 10);
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<T>(getEntityMapper().findPage(filters), 10);
    }


}
