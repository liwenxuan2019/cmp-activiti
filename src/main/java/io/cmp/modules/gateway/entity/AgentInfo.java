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
    //授权渠道 网页聊天:webchat 微信:weixin 微博：weibo QQ:qq 电话：phone
    String authorizationChannel;
    //坐席状态 0:离线 1:在线 2:服务中 3:示忙
    String agentStaus;
    //坐席状态时间
    Date agentStausTime;
    //最大服务数
    int serviceNum;
    //ip地址
    String ipAddress;
    //接入时间
    Date connectTime;
    //断开时间
    Date disconnectTime;
}
