package com.Kz.controller;

import com.Kz.pojo.Result;
import com.Kz.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
public class FileUplodaController {
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        //将文件内容从阿里云存储到本地
        String originalfilename = file.getOriginalFilename();

        String filename = UUID.randomUUID().toString()+originalfilename.substring(originalfilename.lastIndexOf('.')); //生成随机文件名但是后缀不变
        String url = AliOssUtil.uploadFile(filename,file.getInputStream());

        file.transferTo(new File("E:\\IDEA\\JavaWSpace\\PaperManger\\files\\"+filename));
        return Result.success(url);
    }
}
