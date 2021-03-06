package io.cmp.modules.gateway.utils;

public final class ConstElement {
    public ConstElement(){}

    //定义消息是座席还是客户发过来的
    public static final String userType_customer = "customer"; //从客户端发过来的注册消息标识
    public static final String userType_agent = "agent"; //从座席发过来的注册消息标识

    //定义渠道类型：微信原生、微信网页、网页聊天、微博、QQ
    //微信原生
    public static final String channel_weixin = "weixin";
    //微信web渠道
    public static final String channel_weixin_web = "weixin_web";
    //web聊天
    public static final String channel_webchat = "webchat";
    //微博
    public static final String channel_weibo = "weibo";
    //QQ渠道
    public static final String channel_qq = "qq";
    //sms
    public static final String channel_sms = "sms";
    //email
    public static final String channel_email = "email";

    /**
     * 定义消息类型
     * 通知类（连接成功、失败等，不需要后续处理）
     * 命令类（需要进行相应的操作，例如断开与客户的链接）
     * 聊天类（需要转发）
     */
    //通知类：在聊天窗口用灰色底色显示
    public static final String msgType_notice = "notice";
    //命令类：收到后需要做相应的操作
    public static final String msgType_command = "command";
    //交互聊天类：正常聊天内容
    public static final String msgType_chat = "chat";

    /**
     * 定义Event类型：在前端和后端收到消息时的事件类别定义
     * *****原则上按照传输的数据结构来定义事件类型，但为了程序处理方便，同一个结构体会根据消息类型也会拆分多个事件类型处理*****
     * 座席更新状态：服务端收到这个消息后在该消息的函数内处理更新座席状态，数据结构体使用AgentInfo
     * 座席消息类型（双向）：正常的消息，数据结构体使用MessageInfo
     * 客户状态更新类型：客户端的状态如果发生改变，使用该事件定义，数据结构体使用CustomerInfo
     * 客户消息类型（双向）：正常的消息，数据结构体使用MessageInfo
     * 通知类消息：结构体使用MessageInfo。
     * 调试提示类信息：无结构，直接发送字符串。
     */
    //座席更新状态,座席前端使用
    public static final String eventType_agentStatus = "onAgentStatusEvent";
    //座席消息类型（双向），座席前端端使用
    public static final String eventType_agentMsg = "onAgentMessageEvent";
    //客户状态及信息更新，客户前端使用
    public static final String eventType_customerStatus = "onCustomerStatusEvent";
    //客户消息类型（双向），客户前端使用
    public static final String eventType_customerMsg = "onCustomerMessageEvent";
    //通知类消息
    public static final String eventType_notice = "onNoticeMessageEvent";
    //调试提示消息
    public static final String eventType_info = "onInfoMessageEvent";

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
    public static final String commandType_toDisconnect = "toDisconnect";

    /**
     * 发送方定义
     */
    public static final String senderName_customer = "customer"; //客户端发送的消息
    public static final String senderName_agent = "agent"; //人工座席发送的消息
    public static final String senderName_chatRobot = "chatRobot"; //机器人发送的消息
    public static final String senderName_server = "server"; //服务器本身发送的消息

    /**
     * 服务模式定义
     */
    public static final String serviceMode_chatRobot = "chatRobot"; //聊天机器人
    public static final String serviceMode_person = "person"; //人工

}
