

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
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@RestController
@RequestMapping("/file")
//@PropertySource(value = "classpath:application.yml")
public class FileUploadController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${com.upload.location}")
    private String location;

    @PostMapping("/upload")
    public  R upload(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
        // MultipartFile是对当前上传的文件的封装，当要同时上传多个文件时，可以给定多个MultipartFile参数(数组)
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        } else {
            try {
                Date date = new Date();

                // 获取文件名
                String fileName = file.getOriginalFilename();// 获取文件名
                logger.info("fileName=" + fileName);

                // 获取后缀
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                // 重新生成唯一文件名，用于存储数据库
                String newFileName = UUID.randomUUID().toString() + suffixName;

                logger.info("newFileName=" + newFileName);

                String fileDirectory = new SimpleDateFormat("yyyy/MM/dd/").format(date);

                // 文件上产的路径
                String filePath = location + fileDirectory;
                logger.info("filePath=" + filePath);

                File fileCatalog = new File(filePath);
                if(!fileCatalog.exists()){
                    fileCatalog.mkdirs();
                    logger.info("建目录=" + filePath);
                }

                File savefile = new File(filePath, newFileName);
                FileUtils.copyToFile(file.getInputStream(),savefile);
                //file.transferTo(savefile);

                logger.info("原location=" + location);
                String newLocation="";
                if (location.contains("c:")) {
                    newLocation = location.replace("c:", "");
                    logger.info("newLocation=" + newLocation);
                }

                String strBackUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + newLocation + fileDirectory + newFileName;

                logger.info("strBackUrl=" + strBackUrl);
                return R.ok().put("url", strBackUrl);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RRException("上传文件失败");
            }
        }
    }
}
