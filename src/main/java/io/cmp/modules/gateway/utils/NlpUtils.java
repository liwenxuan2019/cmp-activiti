package io.cmp.modules.gateway.utils;

import com.alibaba.fastjson.JSONObject;
import io.cmp.modules.gateway.entity.Nlp;
import io.cmp.modules.sys.service.HttpAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class NlpUtils {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${httpclient.nlpSaaSQAUrl}")
    private String nlpSaaSQAUrl;

    @Autowired
    private HttpAPIService httpAPIService;

    public Nlp nlpSaaSQA(String serviceId, String msgContent, String Channel, String City,String Business)
    {
        Nlp nlp=null;
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("UserID",serviceId);
            map.put("Question",msgContent);
            map.put("Channel",Channel);
            map.put("City",City);
            map.put("Business",Business);
            String httpResult =httpAPIService.doGet(nlpSaaSQAUrl,map);
            logger.info("httpResult="+httpResult);
            nlp= JSONObject.parseObject(httpResult,Nlp.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.info(e.toString());
        }
        return nlp;
    }
}
