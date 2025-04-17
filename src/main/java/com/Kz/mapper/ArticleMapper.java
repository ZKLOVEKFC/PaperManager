package com.Kz.mapper;

import com.Kz.pojo.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {
    //根据id查询目录细节信息
    @Select("select * from article where id =#{id}")
    Article findById(Integer id);

    //新增文章
    @Insert("insert into article (title, content, cover_img, state, category_id, " +
            "create_user, create_time, update_time, abstract_content, innovation_points) " + // 添加了 abstract_content, innovation_points
            "values" +
            "(#{title}, #{content}, #{coverImg}, #{state}, #{categoryId}, " +
            "#{createUser}, #{createTime}, #{updateTime}, #{abstractContent}, #{innovationPoints})") // 添加了 #{abstractContent}, #{innovationPoints}
    void add(Article article);

    //分页查询文章,但是不使用sql语句，使用映射文件中的方法
    List<Article> list(Integer userId, Integer categoryId, String state);

    //更新分类信息
    @Update("update article set " +
            "title = #{title}, " +
            "content = #{content}, " +
            "cover_img = #{coverImg}, " +
            "state = #{state}, " +
            "category_id = #{categoryId}, " +
            "update_time = #{updateTime} " + // updateTime 在 Service 层已设置，这里也一并更新
            "where id = #{id}")
    void update(Article article);
    //只能修改文章标题、分类的更新
    @Update("update article set " +
            "title = #{title}, " +
            "content = #{content}," +
            "category_id = #{categoryId}, " +
            "state = #{state}, " +
            "update_time = #{updateTime} " +
            "where id = #{id}")
    void updateMetadata(Article article); // 参数仍然是 Article，因为 Service 层构造了它

    //删除目录
    @Delete("delete from article where id =#{id}")
    void delete(Integer id);
}
