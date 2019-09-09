package io.cmp.modules.gateway.entity;

import lombok.Data;

import java.util.Date;

@Data

public class MessageInfo {
    //消息发送方id
    private String sourceId;
    //目标客户端id，坐席向外发送消息时要给这个变量赋值，告诉服务器该消息发送给谁
    private String targetId;
    //消息方向 1：客户到坐席 2：坐席到客户
    private String direction;
    //消息类型 网页聊天:webchat 微信:weixin 微博：weibo QQ:qq 电话：phone
    private String msgType;
    //消息内容
    private String msgContent;
    //创建时间
    Date createtime;

}
