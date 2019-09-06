package io.cmp.modules.gateway.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.cmp.modules.gateway.entity.AgentInfo;
import io.cmp.modules.gateway.entity.CustomerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import  io.cmp.modules.gateway.entity.Message;

@Component
@Slf4j
public class MessageEventHandler {

    @Autowired
    private SocketIOServer socketIoServer;

    //坐席端socket连接Map
    public static ConcurrentMap<String, SocketIOClient> agentSocketIOClientMap = new ConcurrentHashMap<>();

    //客户端socket连接Map
    public static ConcurrentMap<String, SocketIOClient> customerSocketIOClientMap = new ConcurrentHashMap<>();


    //坐席端状态Map
    public static ConcurrentMap<String, AgentInfo> agentStatusMap = new ConcurrentHashMap<>();

    //客户端状态Map
    public static ConcurrentMap<String, CustomerInfo> customerStatusMap = new ConcurrentHashMap<>();


    //坐席端&客户端映射Map
    public static ConcurrentMap<String, CustomerInfo> agentAndCustomerMap = new ConcurrentHashMap<>();

    /**
     * 客户端连接的时候触发
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        //获取用户类型 1：坐席 2：客户
        String userType = client.getHandshakeData().getSingleUrlParam("userType");

        if(StringUtils.isNotBlank(userType) && "1".equals(userType))
        {
            //获取坐席sessionId
            String agentSessionId=client.getSessionId().toString();
            //获取坐席工号
            String agentId = client.getHandshakeData().getSingleUrlParam("agentId");
            //获取坐席名称
            String agentnName = client.getHandshakeData().getSingleUrlParam("agentnName");
            //获取坐席授权渠道
            String authorizationChannel = client.getHandshakeData().getSingleUrlParam("authorizationChannel");
            //获取坐席ip地址
            String agentIpAddress=client.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setAgentSessionId(agentSessionId);
            agentInfo.setAgentId(agentId);
            agentInfo.setAgentnName(agentnName);
            agentInfo.setAgentStaus("0");
            agentInfo.setIpAddress(agentIpAddress);
            agentInfo.setConnectTime(connectTime);

            //存储坐席SocketIOClient，用于发送消息
            agentSocketIOClientMap.put(agentId, client);
            //存储坐席状态对象
            agentStatusMap.put(agentId,agentInfo);

            //回发消息
            client.sendEvent("message", "onConnect back");
            log.info("坐席端:" + agentSessionId + " 已连接 agentId=" + agentId+",agentnName=" + agentnName+",坐席授权渠道=" + authorizationChannel+",坐席ip地址=" + agentIpAddress+",接入时间="+connectTime);
        }
        if(StringUtils.isNotBlank(userType) && "2".equals(userType))
        {
            //获取客户sessionId
            String customerSessionId=client.getSessionId().toString();
            //获取客户id
            String customerId = client.getHandshakeData().getSingleUrlParam("customerId");
            //获取客户名称
            String customerName = client.getHandshakeData().getSingleUrlParam("customerName");
            //获取客户访问渠道
            String accessChannel = client.getHandshakeData().getSingleUrlParam("accessChannel");
            //获取客户ip地址
            String customerIpAddress=client.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setCustomerSessionId(customerSessionId);
            customerInfo.setCustomerId(customerId);
            customerInfo.setCustomerName(customerName);
            customerInfo.setIpAddress(customerIpAddress);
            customerInfo.setConnectTime(connectTime);

            //存储客户SocketIOClient，用于发送消息
            customerSocketIOClientMap.put(customerId, client);
            //存储客户状态对象
            customerStatusMap.put(customerId,customerInfo);

            //回发消息
            client.sendEvent("message", "onConnect back");
            log.info("客户端:" + customerSessionId + " 已连接 customerId=" + customerId+",customerName=" + customerName+",客户访问渠道=" + accessChannel+",客户ip地址=" + customerIpAddress+",接入时间="+connectTime);
        }

    }

    /**
     * 客户端关闭连接时触发
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        //获取用户类型 1：坐席 2：客户
        String userType = client.getHandshakeData().getSingleUrlParam("userType");


        if(StringUtils.isNotBlank(userType) && "1".equals(userType)) {
            //获取坐席sessionId
            String agentSessionId=client.getSessionId().toString();
            //获取坐席工号
            String agentId = client.getHandshakeData().getSingleUrlParam("agentId");
            //获取坐席接入结束时间
            Date disconnectTime = new Date();
            log.info("坐席端:" + agentSessionId+ "断开连接"+",断开时间="+disconnectTime);

            agentSocketIOClientMap.remove(agentId);
            agentStatusMap.remove(agentId);
        }

        if(StringUtils.isNotBlank(userType) && "2".equals(userType))
        {
            //获取客户sessionId
            String customerSessionId=client.getSessionId().toString();
            //获取客户id
            String customerId = client.getHandshakeData().getSingleUrlParam("customerId");

            //获取客户接入结束时间
            Date disconnectTime = new Date();

            log.info("客户端:" + customerSessionId + "断开连接"+",断开时间="+disconnectTime);

            customerSocketIOClientMap.remove(customerId);
            customerStatusMap.remove(customerId);

        }

    }

    /**
     * 坐席端事件
     *
     * @param client  　客户端信息
     * @param ackRequest 请求信息
     * @param agentInfo 客户端发送数据
     */
    @OnEvent(value = "agentStatusEvent")
    public void agentStatusEvent(SocketIOClient client, AgentInfo agentInfo, AckRequest ackRequest) {
        log.info("坐席状态：" + agentInfo.toString());
        ackRequest.sendAckData("agentStatusEvent", "服务器收到信息");

        client.sendEvent("agentStatusEvent", "服务器发送的信息");

    }

    /**
     * 坐席端事件
     *
     * @param client  　客户端信息
     * @param ackRequest 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "agentMessageEvent")
    public void agentMessageEvent(SocketIOClient client, Message data ,AckRequest ackRequest) {
        log.info("发来消息：" + data.toString());
        ackRequest.sendAckData("agentMessageEvent", "服务器收到信息");

        client.sendEvent("agentMessageEvent", "服务器发送的信息");


    }

    /**
     * 客户端事件
     *
     * @param client  　客户端信息
     * @param ackRequest 请求信息
     * @param customerInfo 客户端发送数据
     */
    @OnEvent(value = "customerStatusEvent")
    public void customerStatusEvent(SocketIOClient client, CustomerInfo customerInfo, AckRequest ackRequest) {
        log.info("客户状态：" + customerInfo.toString());
        ackRequest.sendAckData("customerStatusEvent", "服务器收到信息");

        client.sendEvent("customerStatusEvent", "服务器发送的信息");

    }

    /**
     * 客户端事件
     *
     * @param client  　客户端信息
     * @param ackRequest 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "customerMessageEvent")
    public void customerMessageEvent(SocketIOClient client, Message data ,AckRequest ackRequest) {
        log.info("发来消息：" + data.toString());
        ackRequest.sendAckData("customerMessageEvent", "服务器收到信息");

        client.sendEvent("customerMessageEvent", "服务器发送的信息");


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
