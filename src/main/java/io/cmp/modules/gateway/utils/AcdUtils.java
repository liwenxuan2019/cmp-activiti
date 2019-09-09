package io.cmp.modules.gateway.utils;

import io.cmp.modules.gateway.entity.AgentInfo;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AcdUtils {
    public static String distribution(ConcurrentMap agentStatusMap)
    {
        String[] keys = (String[])agentStatusMap.keySet().toArray(new String[0]);
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];
        String agentId = (String)agentStatusMap.get(randomKey);
        return agentId;
    }
}
