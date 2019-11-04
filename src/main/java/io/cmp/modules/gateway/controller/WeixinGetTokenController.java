package io.cmp.modules.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.R;
import io.cmp.modules.sys.service.HttpAPIService;
import io.cmp.modules.weixin.entity.CrmWeixinAppidEntity;
import io.cmp.modules.weixin.service.CrmWeixinAppidService;
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
import java.util.Date;
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

    @Autowired
    private CrmWeixinAppidService crmWeixinAppidService;

    @Value("${httpclient.weixinGetTokenUrl}")
    private String weixinGetTokenUrl;

    @Value("${httpclient.weixinGetTicketUrl}")
    private String weixinGetTicketUrl;


    //微信appid对应accessToken Map
    public static ConcurrentMap<String, String> appidToAccessTokenMap= new ConcurrentHashMap<>();

    //微信appid对应ticket Map
    public static ConcurrentMap<String, String> appidToTicketMap= new ConcurrentHashMap<>();


    @GetMapping("/getToken")
    public String getToken(@RequestParam Map<String, Object> params) {
      String appid= (String)params.get("appid");
      String secret=(String)params.get("secret");
      logger.info("appid="+appid);
      logger.info("secret="+secret);

      String result= null;
      String resultTicket= null;
        String accessTokenTemp=appidToAccessTokenMap.get(appid);
        String ticketTemp=appidToTicketMap.get(appid);
        logger.info("accessTokenTemp="+accessTokenTemp);
        logger.info("ticketTemp="+ticketTemp);

        if(null!=accessTokenTemp && StringUtils.isNotBlank(accessTokenTemp)) {
            HashMap resultMap=new HashMap();
            resultMap.put("access_token",accessTokenTemp);
            resultMap.put("expires_in",7200);
            result =JSONObject.toJSONString(resultMap);
            logger.info("result="+result);

            if(null!=ticketTemp && StringUtils.isNotBlank(ticketTemp)) {
                HashMap resultTicketMap=new HashMap();
                resultTicketMap.put("errcode",0);
                resultTicketMap.put("errmsg","ok");
                resultTicketMap.put("ticket",ticketTemp);
                resultTicketMap.put("expires_in",7200);
                resultTicket =JSONObject.toJSONString(resultTicketMap);
                logger.info("resultTicket="+resultTicket);
            }
        }
        else
        {
            try {
                result = httpAPIService.doGet(weixinGetTokenUrl, params);
                logger.info("result=" + result);
                if (result != null && StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String access_token = jsonObject.getString("access_token");
                    logger.info("access_token=" + access_token);

                    if(access_token!=null && StringUtils.isNotBlank(access_token))
                    {
                    appidToAccessTokenMap.put(appid, access_token);

                    CrmWeixinAppidEntity crmWeixinAppidEntity = new CrmWeixinAppidEntity();
                    crmWeixinAppidEntity.setAppid(appid);
                    crmWeixinAppidEntity.setAccessToken(access_token);
                    crmWeixinAppidEntity.setCreateTime(new Date());
                    crmWeixinAppidService.saveOrUpdate(crmWeixinAppidEntity);


                    Map<String, Object> paramsToken = new HashMap<String, Object>();
                    paramsToken.put("access_token", access_token);
                    paramsToken.put("type", "jsapi");
                    resultTicket = httpAPIService.doGet(weixinGetTicketUrl, paramsToken);
                    logger.info("resultTicket=" + resultTicket);
                    if (resultTicket != null && StringUtils.isNotBlank(resultTicket)) {
                        JSONObject jsonObjectTicket = JSONObject.parseObject(resultTicket);
                        String ticket = jsonObjectTicket.getString("ticket");
                        logger.info("ticket=" + ticket);
                    if(ticket!=null && StringUtils.isNotBlank(ticket)) {
                        appidToTicketMap.put(appid, ticket);
                    }
                    }

                }
            }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return resultTicket;
    }
}
