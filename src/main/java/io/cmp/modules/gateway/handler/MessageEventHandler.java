package io.cmp.modules.gateway.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.cmp.modules.gateway.entity.AgentInfo;
import io.cmp.modules.gateway.entity.CustomerInfo;
import io.cmp.modules.gateway.utils.AcdUtils;
import io.cmp.modules.gateway.utils.ConstElement;
import io.netty.handler.codec.MessageAggregationException;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.driver.Const;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import io.cmp.modules.gateway.entity.MessageInfo;

@Component
@Slf4j
public class MessageEventHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SocketIOServer socketIoServer;

    //客户端socket连接Map
    public static ConcurrentMap<String, SocketIOClient> customerSocketIOClientMap = new ConcurrentHashMap<>();
    //坐席端socket连接Map
    public static ConcurrentMap<String, SocketIOClient> agentSocketIOClientMap = new ConcurrentHashMap<>();

    //客户端状态Map
    public static ConcurrentMap<String, CustomerInfo> customerStatusMap = new ConcurrentHashMap<>();
    //坐席端状态Map
    public static ConcurrentMap<String, AgentInfo> agentStatusMap = new ConcurrentHashMap<>();

    //客户端to坐席端映射Map
    public static ConcurrentMap<String, String> customerToAgentMap = new ConcurrentHashMap<>();
    //坐席端to客户端映射Map
    public static ConcurrentMap<String, String> agentToCustomerMap = new ConcurrentHashMap<>();

    //socket对象对应agent的map
    public static ConcurrentMap<SocketIOClient, String> socketToAgentMap = new ConcurrentHashMap<>();
    //socket对象对应customer的Map
    public static ConcurrentMap<SocketIOClient, String> socketToCustomerMap = new ConcurrentHashMap<>();


    /**
     * 客户端连接的时候触发，相当于向消息网关注册后建立连接。
     *
     * @param socketio
     */
    @OnConnect
    public void onConnect(SocketIOClient socketio) {
        //获取用户类型 1：客户 2：坐席
        String userType = socketio.getHandshakeData().getSingleUrlParam("userType");

        //判断是谁来注册的，客户？座席？
        //客户注册
        if(StringUtils.isNotBlank(userType) && ConstElement.userType_customer.equals(userType))
        {
            //获取客户id
            String customerId = socketio.getHandshakeData().getSingleUrlParam("customerId");
            //获取客户名称
            String customerName = socketio.getHandshakeData().getSingleUrlParam("customerName");
            //获取客户访问渠道
            String accessChannel = socketio.getHandshakeData().getSingleUrlParam("accessChannel");
            //获取客户ip地址
            String customerIpAddress=socketio.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setCustomerId(customerId);
            customerInfo.setCustomerName(customerName);
            customerInfo.setAccessChannel(accessChannel);
            customerInfo.setCustomerStaus("1");
            customerInfo.setIpAddress(customerIpAddress);
            customerInfo.setConnectTime(connectTime);

            //存储客户SocketIOClient，用于发送消息
            customerSocketIOClientMap.put(customerId, socketio);
            //存储客户状态对象
            customerStatusMap.put(customerId,customerInfo);
            //存储socket对象对应的客户ID
            socketToAgentMap.put(socketio,customerId);

            //回发消息,通过定义好的方式进行
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setMsgContent("已经建立连接，请等待为您分配座席");
            socketio.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
            logger.info("客户端已连接 customerId=" + customerId+",customerName=" + customerName+",客户访问渠道=" + accessChannel+",客户ip地址=" + customerIpAddress+",接入时间="+connectTime);

            AcdUtils acdUtils =new AcdUtils();
            //分配适合的坐席服务
            String distributionAgentId = acdUtils.distribution(agentStatusMap);

            if(distributionAgentId!=null&&StringUtils.isNotBlank(distributionAgentId)) {
                //建立客户与坐席的对应表Map
                customerToAgentMap.put(customerId, distributionAgentId);
                agentToCustomerMap.put(distributionAgentId, customerId);
            }
            else
            {
                logger.info("没有空闲坐席");
            }
        }
        //座席注册
        else if(StringUtils.isNotBlank(userType) && ConstElement.userType_agent.equals(userType))
        {
            //获取坐席工号
            String agentId = socketio.getHandshakeData().getSingleUrlParam("agentId");
            //获取坐席名称
            String agentnName = socketio.getHandshakeData().getSingleUrlParam("agentnName");
            //获取坐席授权渠道
            String authorizationChannel = socketio.getHandshakeData().getSingleUrlParam("authorizationChannel");
            //获取坐席ip地址
            String agentIpAddress=socketio.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setAgentId(agentId);
            agentInfo.setAgentnName(agentnName);
            agentInfo.setAuthorizationChannel(authorizationChannel);
            agentInfo.setAgentStaus("1");
            agentInfo.setServiceNum(5);
            agentInfo.setIpAddress(agentIpAddress);
            agentInfo.setConnectTime(connectTime);

            //存储坐席SocketIOClient，用于发送消息
            agentSocketIOClientMap.put(agentId, socketio);
            //存储坐席状态对象
            agentStatusMap.put(agentId,agentInfo);
            //存储socket对座席id
            socketToAgentMap.put(socketio,agentId);

            //回发消息
            socketio.sendEvent("message", "onConnect back");
            logger.info("坐席端已连接 agentId=" + agentId+",agentnName=" + agentnName+",坐席授权渠道=" + authorizationChannel+",坐席ip地址=" + agentIpAddress+",接入时间="+connectTime);

        }
        //非法注册消息，直接关闭socket
        else{
            socketio.disconnect();
        }

    }

    /**
     * 客户端关闭连接时触发
     *
     * @param socketio
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient socketio) {
        //通过socket对象找到是哪个socket断开，需要找到客户id或者座席id
        //先查找座席map
        String strFind = "";
        strFind = socketToAgentMap.remove(socketio);
        if(StringUtils.isNotBlank(strFind)){
            logger.info("onDisconnect座席断开，ID="+strFind);
            //删除座席socket Map
            agentSocketIOClientMap.remove(strFind);

            //删除并 获取该坐席服务的客户ID
            String strCusID = agentToCustomerMap.remove(strFind);

            //删除并查找客户socket准备发消息
            SocketIOClient socketCustomer = customerSocketIOClientMap.remove(strCusID);
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setMsgContent("座席已经结束服务");
            socketCustomer.sendEvent(ConstElement.eventType_customerMsg,msgInfo);

            //关闭socket
            socketCustomer.disconnect();
        }

        //查找客户map，看是否是客户端主动断开的
        strFind = socketToCustomerMap.remove(socketio);
        if(StringUtils.isNotBlank(strFind)) {
            logger.info("onDisconnect客户断开，ID="+strFind);
            //删除座席socket Map
            customerSocketIOClientMap.remove(strFind);

            //删除并 获取该坐席服务的客户ID
            String strAgentID = customerToAgentMap.remove(strFind);

            //删除并查找座席socket准备发消息
            SocketIOClient socketAgent = agentSocketIOClientMap.remove(strAgentID);
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setTargetId(strFind); //座席服务的目标客户
            msgInfo.setMsgContent("客户已经断开");
            socketAgent.sendEvent(ConstElement.eventType_customerMsg, msgInfo);

            //关闭socket
            socketAgent.disconnect();
        }

    }



    /**
     * 客户端事件
     *
     * @param client  　客户端信息
     * @param ackRequest 请求信息
     * @param customerInfo 客户端发送数据
     */
    @OnEvent(value = "onCustomerStatusEvent")
    public void onCustomerStatusEvent(SocketIOClient client, CustomerInfo customerInfo, AckRequest ackRequest) {
        logger.info("客户状态：" + customerInfo.toString());
        ackRequest.sendAckData("onCustomerStatusEvent", "服务器收到信息");
        customerStatusMap.replace(customerInfo.getCustomerId(),customerInfo);
        client.sendEvent("onCustomerStatusEvent", "服务器发送的信息");

    }

    /**
     * 客户端事件
     *
     * @param socketIoChannel  　客户端信息
     * @param ackRequest 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "onCustomerMessageEvent")
    public void onCustomerMessageEvent(SocketIOClient socketIoChannel, MessageInfo data , AckRequest ackRequest) {
        logger.info("发来消息：" + data.toString());
        ackRequest.sendAckData("onCustomerMessageEvent", "服务器收到信息");

        //从Messageinfo中获取源ID
        String sourceId=data.getSourceId();

        //通过源ID查询Map获取坐席ID
        if(sourceId!=null&& StringUtils.isNotBlank(sourceId)) {
            String agentId = customerToAgentMap.get(sourceId);

            //通过坐席ID查询到坐席的socket连接对象
            SocketIOClient agentSocketIOClient = agentSocketIOClientMap.get(agentId);

            if (agentSocketIOClient!=null && agentSocketIOClient.isChannelOpen()) {
                //通过坐席socket连接对象转发该消息
                AgentInfo agentInfo = (AgentInfo) agentStatusMap.get(agentId);
                if (agentInfo != null && "1".equals(agentInfo.getAgentStaus())) {
                    MessageInfo sendData = new MessageInfo();
                    sendData.setSourceId(data.getSourceId());
                    sendData.setTargetId(data.getTargetId());
                    sendData.setMsgType(ConstElement.msgType_chat);
                    sendData.setMsgContent(data.getMsgContent());
                    agentSocketIOClient.sendEvent("onCustomerMessageEvent", sendData);
                }
            }
        }
    }



    /**
     * 坐席端事件
     *
     * @param socket  　客户端信息
     * @param ackRequest 请求信息
     * @param agentInfo 客户端发送数据
     */
    @OnEvent(value = "onAgentStatusEvent")
    public void onAgentStatusEvent(SocketIOClient socket, AgentInfo agentInfo, AckRequest ackRequest) {
        logger.info("onAgentStatusEvent收到坐席更新消息：" + agentInfo.toString());

        agentStatusMap.replace(agentInfo.getAgentId(),agentInfo);
        MessageInfo msgInfo = new MessageInfo();
        msgInfo.setMsgType(ConstElement.msgType_notice);
        msgInfo.setMsgContent("座席状态更新成功");
        socket.sendEvent(ConstElement.eventType_agentMsg,msgInfo);

    }

    /**
     * 坐席端事件
     *
     * @param socket  　客户端信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "onAgentMessageEvent")
    public void onAgentMessageEvent(SocketIOClient socket, MessageInfo data) {

        logger.info("onAgentMessageEvent发来消息：" + data.toString());
        socket.sendEvent(ConstElement.eventType_Notice,"收到消息");

        String msgType = data.getMsgType();
        //聊天类型的消息，需要进行转发
        if(msgType.equals(ConstElement.msgType_chat)){
            //从Messageinfo中获取源ID
            String sourceId=data.getSourceId();
            //通过源ID查询Map获取客户ID
            if(sourceId!=null&& StringUtils.isNotBlank(sourceId)) {

                String customerId = agentToCustomerMap.get(sourceId);

                //通过客户ID查询到客户的socket连接对象
                SocketIOClient customerSocketIOClient = customerSocketIOClientMap.get(customerId);

                if (customerSocketIOClient != null && customerSocketIOClient.isChannelOpen()) {

                    //通过客户socket连接对象转发该消息
                    CustomerInfo customerInfo = (CustomerInfo) customerStatusMap.get(customerId);

                    if (customerInfo != null && "1".equals(customerInfo.getCustomerStaus())) {
                        MessageInfo sendData = new MessageInfo();
                        sendData.setSourceId(data.getSourceId());
                        sendData.setTargetId(data.getTargetId());
                        sendData.setMsgType(ConstElement.msgType_chat);
                        sendData.setMsgContent(data.getMsgContent());
                        customerSocketIOClient.sendEvent("onAgentMessageEvent", sendData);
                    }
                }
            }
        }
        //命令类型的消息，针对命令定义的action进行相应的操作
        else if(msgType.equals(ConstElement.msgType_commond)){

        }
        //通知类的消息，做相应的处理
        else if(msgType.equals(ConstElement.msgType_notice)){

        }
        else{
            //非法消息，不作处理
        }

    }



    /**
     * 广播消息
     */
    public void sendBroadcast() {
        for (SocketIOClient client : agentSocketIOClientMap.values()) {
            if (client.isChannelOpen()) {
                client.sendEvent("Broadcast", "当前时间", System.currentTimeMillis());
            }
        }

    }
}
