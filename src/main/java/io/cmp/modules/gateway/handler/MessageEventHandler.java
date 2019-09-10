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
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
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
    public static ConcurrentMap<String, String> customeToAgentMap = new ConcurrentHashMap<>();


    //坐席端to客户端映射Map
    public static ConcurrentMap<String, String> agentToCustomerMap = new ConcurrentHashMap<>();


    /**
     * 客户端连接的时候触发
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        //获取用户类型 1：客户 2：坐席
        String userType = client.getHandshakeData().getSingleUrlParam("userType");

        if(StringUtils.isNotBlank(userType) && "1".equals(userType))
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
            customerInfo.setAccessChannel(accessChannel);
            customerInfo.setCustomerStaus("1");
            customerInfo.setIpAddress(customerIpAddress);
            customerInfo.setConnectTime(connectTime);

            //存储客户SocketIOClient，用于发送消息
            customerSocketIOClientMap.put(customerId, client);
            //存储客户状态对象
            customerStatusMap.put(customerId,customerInfo);

            //回发消息
            client.sendEvent("message", "onConnect back");
            logger.info("客户端:" + customerSessionId + " 已连接 customerId=" + customerId+",customerName=" + customerName+",客户访问渠道=" + accessChannel+",客户ip地址=" + customerIpAddress+",接入时间="+connectTime);

            AcdUtils acdUtils =new AcdUtils();
            //分配适合的坐席服务
            String distributionAgentId = acdUtils.distribution(agentStatusMap);

            if(distributionAgentId!=null&&StringUtils.isNotBlank(distributionAgentId)) {
                //建立客户与坐席的对应表Map
                customeToAgentMap.put(customerId, distributionAgentId);
                agentToCustomerMap.put(distributionAgentId, customerId);
            }
            else
            {
                logger.info("没有空闲坐席");
            }
        }
        if(StringUtils.isNotBlank(userType) && "2".equals(userType))
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
            agentInfo.setAuthorizationChannel(authorizationChannel);
            agentInfo.setAgentStaus("1");
            agentInfo.setServiceNum(5);
            agentInfo.setIpAddress(agentIpAddress);
            agentInfo.setConnectTime(connectTime);

            //存储坐席SocketIOClient，用于发送消息
            agentSocketIOClientMap.put(agentId, client);
            //存储坐席状态对象
            agentStatusMap.put(agentId,agentInfo);

            //回发消息
            client.sendEvent("message", "onConnect back");
            logger.info("坐席端:" + agentSessionId + " 已连接 agentId=" + agentId+",agentnName=" + agentnName+",坐席授权渠道=" + authorizationChannel+",坐席ip地址=" + agentIpAddress+",接入时间="+connectTime);

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
            logger.info("坐席端:" + agentSessionId+ "断开连接"+",断开时间="+disconnectTime);

            agentSocketIOClientMap.remove(agentId);
            agentStatusMap.remove(agentId);
            agentToCustomerMap.remove(agentId);
        }

        if(StringUtils.isNotBlank(userType) && "2".equals(userType))
        {
            //获取客户sessionId
            String customerSessionId=client.getSessionId().toString();
            //获取客户id
            String customerId = client.getHandshakeData().getSingleUrlParam("customerId");

            //获取客户接入结束时间
            Date disconnectTime = new Date();

            logger.info("客户端:" + customerSessionId + "断开连接"+",断开时间="+disconnectTime);

            customerSocketIOClientMap.remove(customerId);
            customerStatusMap.remove(customerId);
            customeToAgentMap.remove(customerId);

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
            String agentId = customeToAgentMap.get(sourceId);

            //通过坐席ID查询到坐席的socket连接对象
            SocketIOClient agentSocketIOClient = agentSocketIOClientMap.get(agentId);

            if (agentSocketIOClient!=null && agentSocketIOClient.isChannelOpen()) {
                //通过坐席socket连接对象转发该消息
                AgentInfo agentInfo = (AgentInfo) agentStatusMap.get(agentId);
                if (agentInfo != null && "1".equals(agentInfo.getAgentStaus())) {
                    MessageInfo sendData = new MessageInfo();
                    sendData.setSourceId(data.getSourceId());
                    sendData.setTargetId(data.getTargetId());
                    sendData.setMsgType("webchat");
                    sendData.setMsgContent(data.getMsgContent());
                    agentSocketIOClient.sendEvent("onCustomerMessageEvent", sendData);
                }
            }
        }
    }



    /**
     * 坐席端事件
     *
     * @param client  　客户端信息
     * @param ackRequest 请求信息
     * @param agentInfo 客户端发送数据
     */
    @OnEvent(value = "onAgentStatusEvent")
    public void onAgentStatusEvent(SocketIOClient client, AgentInfo agentInfo, AckRequest ackRequest) {
        logger.info("坐席状态：" + agentInfo.toString());
        ackRequest.sendAckData("onAgentStatusEvent", "服务器收到信息");
        agentStatusMap.replace(agentInfo.getAgentId(),agentInfo);
        client.sendEvent("onAgentStatusEvent", "服务器发送的信息");

    }

    /**
     * 坐席端事件
     *
     * @param socketIoChannel  　客户端信息
     * @param ackRequest 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "onAgentMessageEvent")
    public void onAgentMessageEvent(SocketIOClient socketIoChannel, MessageInfo data , AckRequest ackRequest) {

        logger.info("发来消息：" + data.toString());
        ackRequest.sendAckData("onAgentMessageEvent", "服务器收到信息");

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
                    sendData.setMsgType("webchat");
                    sendData.setMsgContent(data.getMsgContent());
                    customerSocketIOClient.sendEvent("onAgentMessageEvent", sendData);
                }
            }
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
