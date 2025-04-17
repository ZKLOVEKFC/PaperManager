package com.Kz.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Kz.pojo.Article;
import com.Kz.pojo.PageBean;
import com.Kz.pojo.Result;
import com.Kz.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import com.Kz.pojo.ArticleMetaUpdateDTO; // 引入 DTO
import org.springframework.web.bind.annotation.*;
import com.Kz.service.DeepSeekService; // 导入
import java.util.Map; // 导入Map

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired //注入 DeepSeekService
    private DeepSeekService deepSeekService;

    @PostMapping()
    public Result add(@RequestBody @Validated Article article) {
        articleService.add(article);
        return Result.success();
    }

    /**
     * 分页查询文章列表
     *
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @param categoryId 分类ID (可选)
     * @param state      发布状态 (可选)
     * @return 分页结果，包含文章列表
     */
    @GetMapping
    public Result<PageBean<Article>> list(Integer pageNum, Integer pageSize,
                                          @RequestParam(required = false) Integer categoryId,
                                          @RequestParam(required = false) String state) {
        PageBean<Article> pageBean = articleService.list(pageNum, pageSize, categoryId, state);
        return Result.success(pageBean);
    }

    /**
     * @param article 包含完整文章信息的对象
     */
    @PutMapping
    public Result update(@RequestBody @Validated Article article) {
        articleService.update(article);
        return Result.success();
    }

    /**
     * 更新文章元数据（部分更新）
     *
     * @param articleMetaDTO 包含部分文章信息的 DTO
     */
    @PatchMapping()
    public Result updateMetadata(@RequestBody @Validated ArticleMetaUpdateDTO articleMetaDTO) {
        articleService.updateMetadata(articleMetaDTO); // 调用 Service 层进行更新
        return Result.success();
    }

    /**
     * 删除文章
     *
     * @param id 要删除的文章 ID
     */
    @DeleteMapping()
    public Result delete(Integer id) {
        articleService.delete(id);
        return Result.success();
    }

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class); //
    @PostMapping("/ai/generateInnovationPoints") //
    public Result<String> generateInnovationPoints(@RequestBody Map<String, String> payload) {
        String abstractText = payload.get("abstractText");
        if (abstractText == null || abstractText.trim().isEmpty()) {
            return Result.error("摘要文本不能为空");
        }

        log.info("Received request to generate innovation points.");
        try {
            String generatedPoints = deepSeekService.generateInnovationPoints(abstractText);
            if (generatedPoints != null && !generatedPoints.startsWith("Error:")) {
                log.info("Innovation points generated successfully.");
                return Result.success(generatedPoints); // 将结果放在 data 字段
            } else {
                log.error("Failed to generate innovation points via DeepSeekService. Response: {}", generatedPoints);
                // 返回具体的错误信息给前端
                return Result.error(generatedPoints != null ? generatedPoints : "AI 服务调用失败");
            }
        } catch (Exception e) {
            log.error("Error calling DeepSeekService for innovation points: {}", e.getMessage(), e);
            return Result.error("生成创新点时发生内部错误");
        }
    }
}