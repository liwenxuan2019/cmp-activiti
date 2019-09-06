package io.cmp.modules.gateway.entity;

import lombok.Data;

@Data

public class MessageInfo {
    //源客户端id
    private String sourceClientId;
    //目标客户端id
    private String targetClientId;
    //消息类型
    private String msgType;
    //消息内容
}
