

package io.cmp.modules.gateway.controller;


import io.cmp.common.exception.RRException;
import io.cmp.common.utils.R;

import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@RestController
@RequestMapping("/file")
@PropertySource(value = "classpath:application.yml")
public class FileUploadController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${com.upload.location}")
    private String location;

    @PostMapping("/upload")
    public  R upload(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
        // MultipartFile是对当前上传的文件的封装，当要同时上传多个文件时，可以给定多个MultipartFile参数(数组)
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        }
        Date date = new Date();

        // 获取文件名
        String fileName = file.getOriginalFilename();// 获取文件名
        // 获取后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."))
                ;
        // 重新生成唯一文件名，用于存储数据库
        String newFileName = UUID.randomUUID().toString()+suffixName;

        // 文件上产的路径
        String filePath = location+new SimpleDateFormat("yyyy/MM/dd/").format(date)+newFileName;

        File savefile = new File(filePath);
        //判断上传文件的保存目录是否存在
        if (!savefile.exists() && !savefile.isDirectory()) {
            logger.info(fileName+"目录不存在，需要创建");
            //创建目录
            savefile.mkdir();
        }
        try {
            // FileUtils.copyInputStreamToFile()这个方法里对IO进行了自动操作，不需要额外的再去关闭IO流
            FileUtils.copyInputStreamToFile(file.getInputStream(), savefile);// 复制临时文件到指定目录下
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok().put("url", filePath);
    }


}
