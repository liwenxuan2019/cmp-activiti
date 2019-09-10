package io.cmp.modules.gateway.entity;

import io.cmp.modules.gateway.utils.ConstElement;
import lombok.Data;
import oracle.jdbc.driver.Const;

import java.util.Date;

@Data

public class MessageInfo {

    /**
     * 初始化默认值
     */
    public MessageInfo(){
        this.contentType = ConstElement.contentType_text;
        this.msgChannel = ConstElement.chanType_webchat;
        this.msgType = ConstElement.msgType_notice;
    }
    /**
     * 消息发送方ID
     * 客户发出消息：该字段值为客户ID，转发后该字段值依然是客户ID，座席收到消息后能通过该字段识别消息是哪个客户发过来的
     * 座席发出消息：该字段值为座席ID
     */
    private String sourceId;
    //如果有消息发出发的名字就填上去
    private String sourceName;

    /**
     * 定义消息类型(从常量中选择，不要自行写)
     * 通知类（例如显示客户正在输入等等，不需要后续处理）
     * 命令类（需要进行相应的操作，例如断开与客户的链接、转给机器人、转给人工、转电话等）
     * 聊天类（需要转发）
     */
    private String msgType;

    /**
     * 定义命令变量（常量定义）
     * 转给机器人、转给人工、转电话
     */
    private String commandType;

    /**
     * 消息所属通道（常量定义）
     * 微信原生、微信网页、网页聊天、微博、QQ
     */
    private String msgChannel;

    //消息内容
    private String msgContent;

    /**
     * 内容类型（常量定义）
     * 文本类、图片、附件、音频、视频
     */
    private String contentType;

    //创建时间
    Date createtime;

}
