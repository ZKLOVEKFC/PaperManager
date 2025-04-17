package com.Kz.service;

import com.Kz.pojo.Category;

import java.util.List;

public interface CategoryService {
    //新增文章分类
    void add(Category category);

    //列表查询
    List<Category> list();

    //根据id查询目录细节
    Category findById(Integer id);

    //跟新分类信息
    void update(Category category);

    //删除目录
    void delete(Integer id);
}
