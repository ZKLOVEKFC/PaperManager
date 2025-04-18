package com.Kz.service.impl;

import com.Kz.mapper.CategoryMapper;
import com.Kz.pojo.Category;
import com.Kz.service.CategoryService;
import com.Kz.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        //通过threadlocal获得id
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");

        category.setCreateUser(id);

        categoryMapper.add(category);

    }

    @Override
    public List<Category> list() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        return categoryMapper.list(id);
    }

    @Override
    public Category findById(Integer id) {
        Category categoryDetail = categoryMapper.findById(id);
        return categoryDetail;
    }

    @Override
    public void update(Category category) {
        category.setUpdateTime(LocalDateTime.now());            //设置更新时间
        categoryMapper.update(category);
    }

    @Override
    public void delete(Integer id) {
        Category category = categoryMapper.findById(id);
        if (category ==null)
            throw new RuntimeException("无此目录id");                   //校验传入的id是否存在
        categoryMapper.delete(id);                                                  //通过校验的id才会被送入mapper进行删除
    }

}
