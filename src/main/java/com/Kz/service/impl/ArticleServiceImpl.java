package com.Kz.service.impl;

// 保持你原有的 import 语句

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.Kz.mapper.ArticleMapper;
import com.Kz.pojo.Article;
import com.Kz.pojo.ArticleMetaUpdateDTO;
import com.Kz.pojo.PageBean;
import com.Kz.service.ArticleService;
import com.Kz.utils.ThreadLocalUtil;

// 新增导入
import org.slf4j.Logger;                   // <--- 导入 Logger
import org.slf4j.LoggerFactory;            // <--- 导入 LoggerFactory
import org.springframework.beans.factory.annotation.Autowired; // <--- 导入 Autowired
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service // 确认已有 @Service 注解
public class ArticleServiceImpl implements ArticleService {

    // 添加 Logger
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;            //  已有的注入

    // 新增注入 DeepSeekService
//    @Autowired
//    private DeepSeekService deepSeekService;        // <--- 注入 DeepSeekService

    @Override
    public void add(Article article) {
        // 1. 补充属性值 (原始逻辑)
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);

        // 2. !!移除!! 原本调用 DeepSeekService 生成 innovationPoints 的逻辑

        // 3. 直接添加文章 (现在 article 对象中的 innovationPoints 依赖前端传入)
        articleMapper.add(article);
        log.info("Article '{}' added to database.", article.getTitle());
    }

    // --- 其他方法保持不变 ---

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        // (保持原有实现)
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
        // (保持原有实现)
        // 思考：更新文章时，如果摘要被修改，是否需要重新生成创新点？
        // 当前实现：仅更新时间并调用 mapper.update
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
        log.info("文章 ID '{}' 已更新。", article.getId());
    }

    @Override
    public void updateMetadata(ArticleMetaUpdateDTO articleMetaDTO) {
        // (保持原有实现)
        // 思考：这个 DTO 目前不包含摘要字段，所以无法在此处触发摘要相关的更新。
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
        // (保持原有实现，可以加上日志)
        // 最好先检查文章是否存在
        Article article = articleMapper.findById(id); // 假设 findById 方法存在
        if (article == null) {
            // 可以抛出更具体的异常，或者让全局异常处理器处理
            throw new RuntimeException("无法删除：无此文章 ID " + id);
        }
        articleMapper.delete(id);
        log.info("文章 ID '{}' 已删除。", id);
    }
}