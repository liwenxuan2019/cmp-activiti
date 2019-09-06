package io.cmp.modules.gateway.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerInfo {
    //客户会话id
    String customerSessionId;
    //客户ID
    String customerId;
    //客户姓名
    String customerName;
    //接入渠道 网页聊天:webchart 微信:weixin 微博：weibo QQ:qq 电话：phone
    String accessChannel;
    //客户优先级 
    String customerPriority;
    //ip地址
    String ipAddress;
    //接入时间
    Date connectTime;
    //断开时间
    Date disconnectTime;;
}
