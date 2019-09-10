package io.cmp.modules.gateway.utils;

public final class ConstElement {
    public ConstElement(){}

    //定义消息是座席还是客户发过来的
    public static final String userType_customer = "customer"; //从客户端发过来的注册消息标识
    public static final String userType_agent = "agent"; //从座席发过来的注册消息标识

    //定义渠道类型：微信原生、微信网页、网页聊天、微博、QQ
    //微信原生
    public static final String chanType_weixin = "weixin";
    //微信web渠道
    public static final String chanType_weixin_web = "weixin_web";
    //web聊天
    public static final String chanType_webchat = "webchat";
    //微博
    public static final String chanType_weibo = "weibo";
    //QQ渠道
    public static final String chanType_qq = "qq";
    //sms
    public static final String chanType_sms = "sms";
    //email
    public static final String chanType_email = "email";

    /**
     * 定义消息类型
     * 通知类（连接成功、失败等，不需要后续处理）
     * 命令类（需要进行相应的操作，例如断开与客户的链接）
     * 聊天类（需要转发）
     */
    //通知类
    public static final String msgType_notice = "notice";
    //命令类
    public static final String msgType_command = "command";
    //交互聊天类
    public static final String msgType_chat = "chat";

    /**
     * 定义Event类型：在前端和后端收到消息时的事件类别定义
     * 座席更新状态：服务端收到这个消息后在该消息的函数内处理更新座席状态，数据结构体使用AgentInfo
     * 座席消息类型（双向）：正常的消息，数据结构体使用MessageInfo
     * 客户状态更新类型：客户端的状态如果发生改变，使用该事件定义，数据结构体使用CustomerInfo
     * 客户消息类型（双向）：正常的消息，数据结构体使用MessageInfo
     * 通知类消息：没有指定数据结构体，使用字符串即可。
     */
    //座席更新状态
    public static final String eventType_agentStatus = "onAgentStatusEvent";
    //座席消息类型（双向）
    public static final String eventType_agentMsg = "onAgentMessageEvent";
    //客户状态及信息更新
    public static final String eventType_customerStatus = "onCustomerStatusEvent";
    //客户消息类型（双向）
    public static final String eventType_customerMsg = "onCustomerMessageEvent";
    //通知类
    public static final String eventType_Notice = "onNoticeEvent";

    /**
     * 定义内容类型固定值
     * 文本类、图片、附件、音频、视频
     */
    //文本类
    public static final String contentType_text = "text";
    //图片
    public static final String contentType_pic = "pic";
    //附件
    public static final String contentType_att = "att";
    //音频
    public static final String contentType_audio = "audio";
    //视频
    public static final String contentType_video = "video";

    /**
     * 定义命令消息的类型
     * 转给机器人、转给人工、转电话
     */
    public static final String commandType_toRobot = "toRobot";
    public static final String commandType_toAgent = "toAgent";
    public static final String commandType_toCall = "toCall";

}
