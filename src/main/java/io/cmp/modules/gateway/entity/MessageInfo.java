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
     * 每次会话由座席端生成，每次发送消息时要带上该值，直到该会话结束
     */
    private String serviceId;

    /**
     * 服务模式：机器人Robot;人工Agent;
     */
    private String serviceMode;

    /**
     * 发送方：客户、人工坐席、机器人
     */
    private String senderName;

    /**
     * 座席ID(坐席代码)和座席姓名、坐席昵称
     * 客户发送消息时，如果已经获取到服务的座席了，填写该信息
     * 座席发送消息时ID和姓名必填
     */
    private String agentId;
    private String agentName;
    private String agentNickName; //客户端聊天界面显示该名称

    /**
     * 客户ID和客户姓名
     * 客户发送消息时ID为必填项，姓名可选项
     * 座席向客户发送消息时，ID必填，姓名可选项
     */
    private String customerId;
    private String customerName;


    /**
     * 定义消息类型(从常量中选择，不要自行写)
     * 通知类（例如显示客户正在输入等等，不需要后续处理）
     * 命令     * 命令类（需要进行相应的操作，例如断开与客户的链接、转给机器人、转给人工、转电话等）类（需要进行相应的操作，例如断开与客户的链接、转给机器人、转给人工、转电话等）
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

    //加强型回复消息内容：目前为机器人使用，以后会公用这个。20191018
    private AnswerContent answerContent;

    /**
     * 内容类型（常量定义）
     * 文本类、图片、附件、音频、视频
     * imr 微博评论
     */
    private String contentType;

    //创建时间
    Date createTime;

}
