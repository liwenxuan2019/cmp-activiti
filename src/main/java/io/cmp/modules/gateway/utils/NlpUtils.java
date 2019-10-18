package io.cmp.modules.gateway.utils;

import com.alibaba.fastjson.JSONObject;
import io.cmp.modules.gateway.entity.AnswerContent;
import io.cmp.modules.sys.service.HttpAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NlpUtils {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${httpclient.nlpSaaSQAUrl}")
    private String nlpSaaSQAUrl;

    private static HttpAPIService httpAPIService;

    @Autowired
    public void setHttpAPIService(HttpAPIService httpAPIService)
    {
        NlpUtils.httpAPIService=httpAPIService;
    }

    public AnswerContent nlpSaaSQA(String serviceId, String msgContent, String Channel, String City,String Business)
    {

        AnswerContent answerContent=null;
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("UserID",serviceId);
            map.put("Question",msgContent);
            map.put("Channel",Channel);
            map.put("City",City);
            map.put("Business",Business);
            String httpResult =httpAPIService.doGet(nlpSaaSQAUrl,map);
            logger.info("httpResult="+httpResult);
            answerContent= JSONObject.parseObject(httpResult,AnswerContent.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.info(e.toString());
        }
        return answerContent;
    }
}
