package io.cmp.modules.gateway.utils;

import io.cmp.modules.gateway.entity.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AcdUtils {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static String distribution(ConcurrentMap<String, AgentInfo> agentStatusMap)
    {
        String[] keys = (String[])agentStatusMap.keySet().toArray(new String[0]);
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];
        AgentInfo agentInfo = agentStatusMap.get(randomKey);
        String agentId = agentInfo.getAgentId();
        return agentId;
    }
}
