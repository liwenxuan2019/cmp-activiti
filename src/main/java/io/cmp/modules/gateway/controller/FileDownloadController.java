package io.cmp.modules.gateway.controller;

import io.cmp.common.exception.RRException;
import io.cmp.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/file")
public class FileDownloadController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping("/download")
    public R download(HttpServletRequest request, HttpServletResponse response, @RequestParam String filePath) throws FileNotFoundException {
        logger.info("filePath=" + filePath);
        String strBackUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String newLocation="";
        if (filePath.contains(strBackUrl)) {
            newLocation = filePath.replace(strBackUrl, "c:");
            logger.info("newLocation=" + newLocation);
        }

        File file = new File(newLocation);
        FileInputStream fileInputStream = new FileInputStream(file);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setCharacterEncoding("UTF-8");
        // 设置被下载而不是被打开
        response.setContentType("application/octet-stream");
        // 设置被第三方工具打开,设置下载的文件名
        //response.addHeader("Content-disposition", "attachment;fileName=spring-boot-reference.pdf");
        try {
            OutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RRException("下载文件失败");
        }

        return R.ok();
    }
}
