package io.cmp.modules.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.service.HttpAPIService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/weixin")
public class WeixinGetTokenController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HttpAPIService httpAPIService;

    @Value("${httpclient.weixinGetTokenUrl}")
    private String weixinGetTokenUrl;
    //微信appid对应accessToken Map
    public static ConcurrentMap<String, String> appidToAccessTokenMap= new ConcurrentHashMap<>();


    @GetMapping("/getToken")
    public String getToken(@RequestParam Map<String, Object> params) {
      String appid= (String)params.get("appid");
      String secret=(String)params.get("secret");
      logger.info("appid="+appid);
      logger.info("secret="+secret);

      String result= null;
        String accessTokenTemp=appidToAccessTokenMap.get(appid);
        if(null!=accessTokenTemp && StringUtils.isNotBlank(accessTokenTemp)) {
            HashMap resultMap=new HashMap();
            resultMap.put("access_token",accessTokenTemp);
            resultMap.put("expires_in",7200);
            result =JSONObject.toJSONString(resultMap);
            logger.info("result="+result);
        }
        else
        {
            try {
                result = httpAPIService.doGet(weixinGetTokenUrl, params);
                if (null != result && StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String access_token = jsonObject.getString("access_token");
                    logger.info("access_token=" + access_token);
                    appidToAccessTokenMap.put(appid, access_token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
