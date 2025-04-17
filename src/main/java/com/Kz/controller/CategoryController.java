package com.Kz.controller;

import com.Kz.pojo.Category;
import com.Kz.pojo.Result;
import com.Kz.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result add(@RequestBody @Validated Category category){               //创建目录
        categoryService.add(category);
        return Result.success();
    }


    @GetMapping
    public Result<List<Category>> list(){                                                                       //列表查询
        List<Category> cs = categoryService.list();
        return Result.success(cs);
    }

    @GetMapping("/detail")
    public Result<Category> detail(Integer id){                                                             //根据目录id获取目录信息
        Category categoryDetail = categoryService.findById(id);
        if (categoryDetail ==null){
            return Result.error("查无此分类");
        }
        return Result.success(categoryDetail);
    }

    @PutMapping
    public Result update(@RequestBody @Validated Category category){            //更新目录信息
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping()
    public Result delete(Integer id){
        categoryService.delete(id);
        return Result.success();
    }

}
