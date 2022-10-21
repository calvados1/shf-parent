package com.atguigu.base;

import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface BaseMapper<T> {
    void insert(T t);

    void insertBatch(List<T> list);

    void delete(Long id);

    void update(T t);

    T getById(Long id);

    Page<T> findPage(Map<String, Object> filters);
}