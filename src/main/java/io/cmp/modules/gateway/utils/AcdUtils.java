package io.cmp.modules.gateway.utils;

import io.cmp.modules.gateway.entity.AgentInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

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

    /**
     * 获取合适的座席
     * @param agentStatusMap
     * @param customerToAgentMap
     * @return
     */
    public String getCompatibleAgent(ConcurrentMap<String, AgentInfo> agentStatusMap, ConcurrentMap<String, String> customerToAgentMap)
    {
        /**
         * 根据座席当前服务座席的情况（实时），平均分配
         */
        // 1、根据客户对应的座席服务对应表获取当前座席服务的客户数量对应表
        Map<String, Integer> agentToNumMap = new HashMap<>();
        for (ConcurrentMap.Entry<String, String> entry : customerToAgentMap.entrySet()) {
            if (agentToNumMap.containsKey(entry.getValue())) {
                agentToNumMap.put(entry.getValue(), agentToNumMap.get(entry.getValue()) + 1);
            } else {
                agentToNumMap.put(entry.getValue(), 1);
            }
        }

        logger.info("getCompatibleAgent==> 分配前customerToAgentMap.size = " + customerToAgentMap.size() + ";values=" + customerToAgentMap.toString());
        logger.info("getCompatibleAgent==> 分配前agentToNumMap = " + agentToNumMap.toString());

        logger.info("getCompatibleAgent==> 分配前agentStatusMap = " + agentStatusMap.size() + "values=" + agentStatusMap.toString());
        // 2、根据座席状态对agentToNumMap进行处理，如果有忙线座席将他从map中删除，如果有空闲map中没有，加入并设置值为0
        for (ConcurrentMap.Entry<String, AgentInfo> entry : agentStatusMap.entrySet()) {
            AgentInfo agentInfo = entry.getValue();
            String agentStatus = agentInfo.getAgentStatus();
            String agentId = agentInfo.getAgentId();
            if (StringUtils.isBlank(agentStatus) || StringUtils.isBlank(agentId)) {
                continue;
            }

            // 2.1 排除掉非可用状态座席（小休、忙等），添加可用状态并且没有在map表中的座席
            if (agentStatus.equals("1")) { //在线座席
                if (!agentToNumMap.containsKey(agentInfo.getAgentId())) {  //没有在map中，添加进去
                    agentToNumMap.put(agentInfo.getAgentId(), 0);
                }
            } else { //其他状态
                agentToNumMap.remove(agentInfo.getAgentId());
            }

            // 2.2 判断正在服务客户的数量，是否已经超过最大服务数量
            int maxServiceNum = agentInfo.getServiceNum();
            if (agentToNumMap.containsKey(agentId)) {
                if (agentToNumMap.get(agentId) == maxServiceNum) {
                    agentToNumMap.remove(agentId);
                }
            }
        }

        // 3、从agentTo NumMap中循环，查找key值是否在agentStatusMap中，不在agentStatus中说明座席已经断开，需要从agentToNumMap删除。
        Iterator<ConcurrentMap.Entry<String, Integer>> it = agentToNumMap.entrySet().iterator();
        while (it.hasNext()) {
            ConcurrentMap.Entry<String, Integer> entry = it.next();
            if (!agentStatusMap.containsKey(entry.getKey())) {
                logger.info("getCompatibleAgent 第三步在agentToNumMap发现无效（不在agentStatusMap中）数据，agentId=" + entry.getKey());
                it.remove();
            }
        }

        // 3、对agentToNumMap的值进行从小到大排序
        List<Map.Entry<String, Integer>> sortList = new ArrayList(agentToNumMap.entrySet());
        logger.info("getCompatibleAgent==> 排序前sortList = " + sortList.toString());
        AtomicReference<String> retAgent = new AtomicReference<>("");
        if (sortList.size() > 0) {
            Collections.sort(sortList, (o1, o2) -> (o1.getValue() - o2.getValue()));

            logger.info("getCompatibleAgent==> 排序后sortList = " + sortList.toString());

            retAgent.set(sortList.get(0).getKey());
            return retAgent.get();
        } else {
            return retAgent.get();
        }
    }
}
