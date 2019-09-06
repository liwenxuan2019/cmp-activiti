package io.cmp.modules.gateway.entity;
import lombok.Data;

import	java.util.Date;

@Data
public class AgentInfo {
    //坐席会话id
    String agentSessionId;
    //坐席工号
    String agentId;
    //坐席姓名
    String agentnName;
    //授权渠道 网页聊天:webchart 微信:weixin 微博：weibo QQ:qq 电话：phone
    String authorizationChannel;
    //坐席状态 0:就绪 1：服务中 2：示忙
    String agentStaus;
    //坐席状态时间
    Date agentStausTime;
    //ip地址
    String ipAddress;
    //最大服务数
    int serviceNum;
    //接入时间
    Date connectTime;
    //断开时间
    Date disconnectTime;
}
