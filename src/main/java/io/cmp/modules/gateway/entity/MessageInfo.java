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

    /**
     * 定义消息类型(从常量中选择，不要自行写)
     * 通知类（连接成功、失败等，不需要后续处理）
     * 命令类（需要进行相应的操作，例如断开与客户的链接）
     * 聊天类（需要转发）
     */
    private String msgType;

    //消息所属通道：微信原生、微信网页、网页聊天、微博、QQ
    private String msgChannel;

    //消息内容
    private String msgContent;

    /**
     * 内容类型
     * 文本类、图片、附件、音频、视频
     */
    private String contentType;

    //创建时间
    Date createtime;

}
