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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    public static ConcurrentMap<String, SocketIOClient> customerToSocketMap = new ConcurrentHashMap<>();
    //坐席端socket连接Map
    public static ConcurrentMap<String, SocketIOClient> agentToSocketMap = new ConcurrentHashMap<>();

    //客户端状态Map
    public static ConcurrentMap<String, CustomerInfo> customerStatusMap = new ConcurrentHashMap<>();
    //坐席端状态Map
    public static ConcurrentMap<String, AgentInfo> agentStatusMap = new ConcurrentHashMap<>();

    //客户端to坐席端映射Map
    public static ConcurrentMap<String, String> customerToAgentMap = new ConcurrentHashMap<>();
    //坐席端to客户端映射Map
    public static ConcurrentMap<String, String> agentToCustomerMap = new ConcurrentHashMap<>();

    //socket对象sessionID对应agent的map
    public static ConcurrentMap<String, String> socketToAgentMap = new ConcurrentHashMap<>();
    //socket对象sessionID对应customer的Map
    public static ConcurrentMap<String, String> socketToCustomerMap = new ConcurrentHashMap<>();

    //客户排队列表信息，供分配算法使用
    public static List<String> customerQueue = new LinkedList<String>();

    /**
     * 客户端连接的时候触发，相当于向消息网关注册后建立连接。
     *
     * @param socket
     */
    @OnConnect
    public void onConnect(SocketIOClient socket) {
        String socketKey = socket.getSessionId() + socket.getRemoteAddress().toString();
        logger.info("socketKey is { }", socketKey);

        //获取用户类型 1：客户 2：坐席
        String userType = socket.getHandshakeData().getSingleUrlParam("userType");

        //判断是谁来注册的，客户？座席？
        //客户注册
        if(StringUtils.isNotBlank(userType) && ConstElement.userType_customer.equals(userType))
        {
            //获取客户id
            String customerId = socket.getHandshakeData().getSingleUrlParam("customerId");
            //获取客户名称
            String customerName = socket.getHandshakeData().getSingleUrlParam("customerName");
            //获取客户访问渠道
            String accessChannel = socket.getHandshakeData().getSingleUrlParam("accessChannel");
            //获取客户ip地址
            String customerIpAddress=socket.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setCustomerId(customerId);
            customerInfo.setCustomerName(customerName);
            customerInfo.setAccessChannel(accessChannel);
            customerInfo.setCustomerStatus("1");
            customerInfo.setIpAddress(customerIpAddress);
            customerInfo.setConnectTime(connectTime);

            //存储客户SocketIOClient，用于发送消息
            customerToSocketMap.put(customerId, socket);
            //存储客户状态对象
            customerStatusMap.put(customerId,customerInfo);
            //存储socket对象对应的客户ID

            String socketCustomerKey = socket.getSessionId() + socket.getRemoteAddress().toString();
            socketToCustomerMap.put(socketCustomerKey,customerId);

            //回发消息,通过定义好的方式进行
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setMsgContent("客户端已经建立连接，请等待为您分配座席");
            socket.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
            logger.info("客户端已连接 customerId=" + customerId+",customerName=" + customerName+",客户访问渠道=" + accessChannel+",客户ip地址=" + customerIpAddress+",接入时间="+connectTime);

            //将客户ID添加到待分配队列
            customerQueue.add(customerId);
            //调用分配算法分配
            allocateAgent();

        }
        //座席注册
        else if(StringUtils.isNotBlank(userType) && ConstElement.userType_agent.equals(userType))
        {
            //获取坐席工号
            String agentId = socket.getHandshakeData().getSingleUrlParam("agentId");
            //获取坐席名称
            String agentnName = socket.getHandshakeData().getSingleUrlParam("agentName");
            //获取坐席授权渠道
            String authorizationChannel = socket.getHandshakeData().getSingleUrlParam("authorizationChannel");
            //获取坐席ip地址
            String agentIpAddress=socket.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setAgentId(agentId);
            agentInfo.setAgentName(agentnName);
            agentInfo.setAuthorizationChannel(authorizationChannel);
            agentInfo.setAgentStatus("1");
            agentInfo.setServiceNum(5);
            agentInfo.setIpAddress(agentIpAddress);
            agentInfo.setConnectTime(connectTime);

            //存储坐席SocketIOClient，用于发送消息
            agentToSocketMap.put(agentId, socket);
            //存储坐席状态对象
            agentStatusMap.put(agentId,agentInfo);
            //存储socket对座席id
            String socketAgentKey = socket.getSessionId() + socket.getRemoteAddress().toString();
            socketToAgentMap.put(socketAgentKey,agentId);

            //回发消息,通过定义好的方式进行
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setMsgContent("座席端已经建立连接，开始服务");
            //回发消息
            socket.sendEvent(ConstElement.eventType_agentMsg, msgInfo);
            logger.info("坐席端已连接 agentId=" + agentId+",agentnName=" + agentnName+",坐席授权渠道=" + authorizationChannel+",坐席ip地址=" + agentIpAddress+",接入时间="+connectTime);

            //调用分配算法分配
            allocateAgent();
        }
        //非法注册消息，直接关闭socket
        else{
            socket.sendEvent(ConstElement.eventType_Notice,"你是非法注册用户");
            logger.info("你是非法注册用户" );
            socket.disconnect();

        }

    }

    /**
     * 客户端关闭连接时触发
     *
     * @param socket
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient socket) {

        String socketKey = socket.getSessionId() + socket.getRemoteAddress().toString();
        logger.info("socketKey is {}", socketKey);

        //通过socket对象找到是哪个socket断开，需要找到客户id或者座席id
        //先查找座席map
        String strFindAgent = socketToAgentMap.get(socketKey);
        String strFindCustomer = socketToCustomerMap.get(socketKey);
        if(StringUtils.isNotBlank(strFindAgent)){
            socketToAgentMap.remove(socketToAgentMap);
            logger.info("onDisconnect 座席端断开，strFindAgent="+strFindAgent);

            //删除座席socket Map
            agentToSocketMap.remove(strFindAgent);

            //删除并 获取该坐席服务的客户ID
            logger.info("onDisconnect==>agentToCustomerMap="+agentToCustomerMap.toString());
            String strCusID = agentToCustomerMap.get(strFindAgent);
            logger.info("strCusID="+strCusID);
            agentToCustomerMap.remove(strFindAgent);

            if(StringUtils.isNotBlank(strCusID)) {
                //删除并查找客户socket准备发消息
                SocketIOClient socketCustomer = customerToSocketMap.remove(strCusID);
                if(null != socketCustomer){
                    MessageInfo msgInfo = new MessageInfo();
                    msgInfo.setMsgType(ConstElement.msgType_notice);
                    msgInfo.setMsgContent("座席已经结束服务");
                    socketCustomer.sendEvent(ConstElement.eventType_customerMsg, msgInfo);

                    //关闭socket
                    socketCustomer.disconnect();
                }

                //触发分配座席
                allocateAgent();
            }
        }

        //查找客户map，看是否是客户端主动断开的

        if(StringUtils.isNotBlank(strFindCustomer)) {
            socketToCustomerMap.remove(strFindCustomer);
            logger.info("onDisconnect 客户端断开，strFindCustomer="+strFindCustomer);

            //删除座席socket Map
            customerToSocketMap.remove(strFindCustomer);

            //删除并 获取该服务该客户的座席
            String strAgentID = customerToAgentMap.remove(strFindCustomer);
            logger.info("strAgentID="+strAgentID);
            if(StringUtils.isNotBlank(strAgentID)) {
                //找到该座席的socket，命令座席这个客户已经断开，可以结束这个座席的服务了
                SocketIOClient socketAgent = agentToSocketMap.get(strAgentID);
                if(null != socketAgent){
                    MessageInfo msgInfo = new MessageInfo();
                    msgInfo.setMsgType(ConstElement.msgType_command);
                    msgInfo.setTargetId(strFindCustomer);
                    msgInfo.setSourceId(strFindCustomer); //座席服务的目标客户
                    msgInfo.setCommandType(ConstElement.commandType_toDisconnect);
                    msgInfo.setMsgContent("客户已经断开");
                    socketAgent.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
                }

                //触发分配座席
                allocateAgent();
            }
        }

    }


    /**
     * 客户端事件
     *
     * @param socket  　客户端信息
     * @param ackRequest 请求信息
     * @param customerInfo 客户端发送数据
     */
    @OnEvent(value = "onCustomerStatusEvent")
    public void onCustomerStatusEvent(SocketIOClient socket, CustomerInfo customerInfo, AckRequest ackRequest) {
        logger.info("onCustomerStatusEvent 客户状态信息：" + customerInfo.toString());

        customerStatusMap.replace(customerInfo.getCustomerId(),customerInfo);
        MessageInfo msgInfo = new MessageInfo();
        msgInfo.setMsgType(ConstElement.msgType_notice);
        msgInfo.setMsgContent("客户状态更新成功");
        socket.sendEvent(ConstElement.eventType_customerMsg,msgInfo);

    }

    /**
     * 客户端事件
     *
     * @param socket  　客户端信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "onCustomerMessageEvent")
    public void onCustomerMessageEvent(SocketIOClient socket, MessageInfo data) {
        logger.info("onCustomerMessageEvent 发来消息：" + data.toString());
        socket.sendEvent(ConstElement.eventType_Notice,"收到你发来的消息"+data.toString());

        //从Messageinfo中获取源ID
        String sourceId=data.getSourceId();

        //通过源ID查询Map获取坐席ID
        if(sourceId!=null&& StringUtils.isNotBlank(sourceId)) {
            String agentId = customerToAgentMap.get(sourceId);

            //通过坐席ID查询到坐席的socket连接对象
            SocketIOClient agentSocketIOClient = agentToSocketMap.get(agentId);

            if (agentSocketIOClient!=null && agentSocketIOClient.isChannelOpen()) {
                //通过坐席socket连接对象转发该消息
                AgentInfo agentInfo = (AgentInfo) agentStatusMap.get(agentId);
                if (agentInfo != null && "1".equals(agentInfo.getAgentStatus())) {
                    MessageInfo sendData = new MessageInfo();
                    sendData.setSourceId(data.getSourceId());
                    sendData.setMsgType(ConstElement.msgType_chat);
                    sendData.setMsgContent(data.getMsgContent());
                    agentSocketIOClient.sendEvent("onCustomerMessageEvent", sendData);
                }
            }
        }
    }



    /**
     * 坐席端更新状态事件
     *
     * @param socket  　客户端信息
     * @param agentInfo 客户端发送数据
     */
    @OnEvent(value = "onAgentStatusEvent")
    public void onAgentStatusEvent(SocketIOClient socket, AgentInfo agentInfo, AckRequest ackRequest) {
        logger.info("onAgentStatusEvent 坐席状态信息：" + agentInfo.toString());

        agentStatusMap.replace(agentInfo.getAgentId(),agentInfo);
        MessageInfo msgInfo = new MessageInfo();
        msgInfo.setMsgType(ConstElement.msgType_notice);
        msgInfo.setMsgContent("座席状态更新成功");
        socket.sendEvent(ConstElement.eventType_agentMsg,msgInfo);

    }

    /**
     * 坐席端消息事件
     *
     * @param socket  　客户端信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "onAgentMessageEvent")
    public void onAgentMessageEvent(SocketIOClient socket, MessageInfo data) {

        logger.info("onAgentMessageEvent 发来消息：" + data.toString());
        socket.sendEvent(ConstElement.eventType_Notice,"收到你发来的消息"+data.toString());

        String msgType = data.getMsgType();
        //聊天类型的消息，需要进行转发
        if(msgType.equals(ConstElement.msgType_chat)){
            //从Messageinfo中获取源ID
            String sourceId=data.getSourceId();
            //通过源ID查询Map获取客户ID
            if(sourceId!=null&& StringUtils.isNotBlank(sourceId)) {

                String customerId = agentToCustomerMap.get(sourceId);
                logger.info("customerId="+customerId);

                //通过客户ID查询到客户的socket连接对象
                SocketIOClient customerSocketIOClient = customerToSocketMap.get(customerId);

                if (customerSocketIOClient != null && customerSocketIOClient.isChannelOpen()) {

                    //通过客户socket连接对象转发该消息
                    CustomerInfo customerInfo = (CustomerInfo) customerStatusMap.get(customerId);

                    if (customerInfo != null && "1".equals(customerInfo.getCustomerStatus())) {
                        MessageInfo msgInfo = new MessageInfo();
                        msgInfo.setSourceId(data.getSourceId());
                        msgInfo.setMsgType(ConstElement.msgType_chat);
                        msgInfo.setMsgContent(data.getMsgContent());
                        customerSocketIOClient.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
                    }
                }
            }
        }
        //命令类型的消息，针对命令定义的action进行相应的操作
        else if(msgType.equals(ConstElement.msgType_command)){

        }
        //通知类的消息，做相应的处理
        else if(msgType.equals(ConstElement.msgType_notice)){

        }
        else{
            //非法消息，不作处理
        }
    }

    /**
     *
     */
    private void allocateAgent(){
        logger.info("客户队列里有"+customerQueue.size()+"个客户等待");
        if(customerQueue.size() == 0){
            return;
        }

        String customerId = customerQueue.get(0);

        AcdUtils acdUtils =new AcdUtils();
        //分配适合的坐席服务
        String allocAgentId = acdUtils.distribution(agentStatusMap);

        if(allocAgentId!=null&&StringUtils.isNotBlank(allocAgentId)) {
            //建立客户与坐席的对应表Map
            customerToAgentMap.put(customerId, allocAgentId);
            logger.info("customerid="+customerId+ "分配的座席ID="+allocAgentId);
            logger.info("customerToAgentMap"+customerToAgentMap.toString());
            agentToCustomerMap.put(allocAgentId, customerId);
            logger.info("agentToCustomerMap"+agentToCustomerMap.toString());

            MessageInfo msgInfoAgent = new MessageInfo();

            //给座席发命令消息服务新的客户
            msgInfoAgent.setSourceId(customerId);
            msgInfoAgent.setMsgType(ConstElement.msgType_command);
            msgInfoAgent.setCommandType(ConstElement.commandType_toAgent);
            msgInfoAgent.setMsgContent("为你分配了新的客户,ID="+customerId);
            SocketIOClient socketAgent = agentToSocketMap.get(allocAgentId);
            socketAgent.sendEvent(ConstElement.eventType_agentMsg,msgInfoAgent);

            //给客户发通知消息
            SocketIOClient socketCustomer = customerToSocketMap.get(customerId);
            MessageInfo msgInfoCus = new MessageInfo();
            msgInfoCus.setSourceId(allocAgentId);
            msgInfoCus.setMsgType(ConstElement.msgType_notice);
            msgInfoCus.setMsgContent("已经为您分配座席");
            socketCustomer.sendEvent(ConstElement.eventType_customerMsg,msgInfoCus);


            customerQueue.remove(0);
         }
         else {
             logger.info("没有空闲坐席");
         }

    }


    /**
     * 广播消息
     */
    public void sendBroadcast() {
        for (SocketIOClient client : agentToSocketMap.values()) {
            if (client.isChannelOpen()) {
                client.sendEvent("Broadcast", "当前时间", System.currentTimeMillis());
            }
        }

    }
}
