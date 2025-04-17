package com.Kz.service;

import com.Kz.pojo.Article;
import com.Kz.pojo.ArticleMetaUpdateDTO;
import com.Kz.pojo.PageBean;

public interface ArticleService {
    //新增文章
    void add(Article article);

    //分页查询文章
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);

    //跟新文章信息
    void update(Article article);
    //只能修改文章标题、分类的更新
    void updateMetadata(ArticleMetaUpdateDTO articleMetaDTO); // 新增方法,只更新文章的部分属性，title和categoryId

    void delete(Integer id);
}
