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
    //接入渠道（常量定义）
    String accessChannel;
    //客户状态 0:离线 1:在线
    String customerStatus;
    //客户优先级 
    int customerPriority;
    //ip地址
    String ipAddress;
    //接入时间
    Date connectTime;
    //断开时间
    Date disconnectTime;;
}
