package com.Kz.service.impl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.Kz.mapper.ArticleMapper;
import com.Kz.pojo.Article;
import com.Kz.pojo.ArticleMetaUpdateDTO;
import com.Kz.pojo.PageBean;
import com.Kz.service.ArticleService;
import com.Kz.utils.ThreadLocalUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void add(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);

        articleMapper.add(article);
        log.info("Article '{}' added to database.", article.getTitle());
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        PageBean<Article> pageBean = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        List <Article> as = articleMapper.list(userId, categoryId, state);
        Page<Article> page = (Page<Article>) as;
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public void update(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
        log.info("文章 ID '{}' 已更新。", article.getId());
    }

    @Override
    public void updateMetadata(ArticleMetaUpdateDTO articleMetaDTO) {
        Article articleToUpdate = new Article();
        articleToUpdate.setId(articleMetaDTO.getId());
        articleToUpdate.setTitle(articleMetaDTO.getTitle());
        articleToUpdate.setContent(articleMetaDTO.getContent());
        articleToUpdate.setState(articleMetaDTO.getState());
        articleToUpdate.setCategoryId(articleMetaDTO.getCategoryId());
        articleToUpdate.setUpdateTime(LocalDateTime.now());
        articleMapper.updateMetadata(articleToUpdate);
        log.info("文章 ID '{}' 的元数据已更新。", articleMetaDTO.getId());
    }

    @Override
    public void delete(Integer id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("无法删除：无此文章 ID " + id);
        }
        articleMapper.delete(id);
        log.info("文章 ID '{}' 已删除。", id);
    }
}