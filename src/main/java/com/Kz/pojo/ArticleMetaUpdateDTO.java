                            //只能修改文章标题、分类的更新
// itheima/pojo/ArticleMetaUpdateDTO.java
package com.Kz.pojo;
import com.Kz.anno.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size; // 引入 Size
import lombok.Data;

@Data
public class ArticleMetaUpdateDTO {
    @NotNull // 更新时 ID 必须提供
    private Integer id;

    @NotEmpty
    // @Pattern(regexp = "^\\S{1,100}$") // 移除或修改这个过于严格的正则
    @Size(min = 1, max = 100) // 使用 Size 限制长度更合适
    private String title; // 文章标题

    @NotEmpty
    private String content;//文章内容

    @State
    private String state;//发布状态 已发布|草稿

    @NotNull // 分类 ID 必须提供
    private Integer categoryId; // 文章分类id
}