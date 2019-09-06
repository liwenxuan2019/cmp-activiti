package io.cmp.modules.gateway.entity;

import lombok.Data;

@Data

public class MessageInfo {
    //源客户端id
    private String sourceClientId;
    //目标客户端id
    private String targetClientId;
    //消息方向 1：客户坐席 2：坐席到客户
    private String direction;
    //消息类型 chart
    private String msgType;
    //消息内容
    private String msgContent;

}
