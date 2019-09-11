package io.cmp.modules.gateway.utils;

import io.cmp.modules.gateway.entity.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AcdUtils {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public String distribution(ConcurrentMap<String, AgentInfo> agentStatusMap)
    {
        String[] keys = (String[])agentStatusMap.keySet().toArray(new String[0]);

        if(keys.length == 0){
            logger.info("AcdUtils=没有座席登录");
            return "";
        }

        String agentId=null;
        while(true) {
            Random random = new Random();
            String randomKey = keys[random.nextInt(keys.length)];
            logger.info("randomKey=" + randomKey);
            AgentInfo agentInfo = agentStatusMap.get(randomKey);
            if("1".equals(agentInfo.getAgentStatus()) || "2".equals(agentInfo.getAgentStatus())) {
                agentId = agentInfo.getAgentId();
                logger.info("agentId=" + agentId);
                break;
            }
            else
            {
                logger.info("AcdUtils=没有空闲坐席");
            }
        }

        return agentId;
    }
}
