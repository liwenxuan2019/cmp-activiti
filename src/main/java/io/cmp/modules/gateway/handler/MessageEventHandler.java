package io.cmp.modules.gateway.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.cmp.common.utils.HttpResult;
import io.cmp.modules.gateway.entity.AgentInfo;
import io.cmp.modules.gateway.entity.CustomerInfo;
import io.cmp.modules.gateway.utils.AcdUtils;
import io.cmp.modules.gateway.utils.ConstElement;
import io.cmp.modules.sys.service.HttpAPIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import io.cmp.modules.gateway.entity.MessageInfo;
import org.springframework.beans.factory.annotation.Value;

@Component
@Slf4j
public class MessageEventHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SocketIOServer socketIoServer;

    @Autowired
    private HttpAPIService httpAPIService;

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
    //public static ConcurrentMap<String, String> agentToCustomerMap = new ConcurrentHashMap<>();

    //socket对象sessionID对应agent的map
    public static ConcurrentMap<String, String> socketSessionToAgentMap = new ConcurrentHashMap<>();
    //socket对象sessionID对应customer的Map
    public static ConcurrentMap<String, String> socketSessionToCustomerMap = new ConcurrentHashMap<>();

    //客户排队列表信息，供分配算法使用
    public static List<String> customerQueue = new LinkedList<String>();

    @Value("${httpclient.crmCustomerInfoSaveUrl}")
    private String crmCustomerInfoSaveUrl;

    @Value("${httpclient.crmCustomerInfoUpdateUrl}")
    private String crmCustomerInfoUpdateUrl;

    @Value("${httpclient.crmAgentInfoSaveUrl}")
    private String crmAgentInfoSaveUrl;

    @Value("${httpclient.crmAgentInfoUpdateUrl}")
    private String crmAgentInfoUpdateUrl;

    @Value("${httpclient.crmmessageinfoSaveUrl}")
    private String crmmessageinfoSaveUrl;

    /**
     * 客户端连接的时候触发，相当于向消息网关注册后建立连接。
     *
     * @param socket
     */
    @OnConnect
    public void onConnect(SocketIOClient socket) {
        String socketKey = socket.getSessionId() + socket.getRemoteAddress().toString();
        logger.info("onConnect socketKey is ="+ socketKey);

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
            socketSessionToCustomerMap.put(socketCustomerKey,customerId);

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

            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("customerSessionId",socket.getSessionId());
                map.put("customerId",customerId);
                map.put("customerName",customerName);
                map.put("accessChannel",accessChannel);
                map.put("customerStatus","1");
                map.put("customerPriority","1");
                map.put("ipAddress",customerIpAddress);
                map.put("connectTime",connectTime);
                HttpResult httpResult =httpAPIService.doPost(crmCustomerInfoSaveUrl,map);
                logger.info(httpResult.getBody());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                logger.info(e.toString());
            }

        }
        //座席注册
        else if(StringUtils.isNotBlank(userType) && ConstElement.userType_agent.equals(userType))
        {
            //获取坐席工号
            String agentId = socket.getHandshakeData().getSingleUrlParam("agentId");
            //获取坐席名称
            String agentName = socket.getHandshakeData().getSingleUrlParam("agentName");
            //获取坐席授权渠道
            String authorizationChannel = socket.getHandshakeData().getSingleUrlParam("authorizationChannel");
            //获取坐席ip地址
            String agentIpAddress=socket.getRemoteAddress().toString();
            //获取坐席接入开始时间
            Date connectTime = new Date();

            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setAgentId(agentId);
            agentInfo.setAgentName(agentName);
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
            socketSessionToAgentMap.put(socketAgentKey,agentId);

            //回发消息,通过定义好的方式进行
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setMsgContent("座席端已经建立连接，开始服务");
            //回发消息
            socket.sendEvent(ConstElement.eventType_agentMsg, msgInfo);
            logger.info("坐席端已连接 agentId=" + agentId+",agentName=" + agentName+",坐席授权渠道=" + authorizationChannel+",坐席ip地址=" + agentIpAddress+",接入时间="+connectTime);

            //调用分配算法分配
            allocateAgent();

            try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("agentSessionId",socket.getSessionId());
			map.put("agentId",agentId);
			map.put("agentCode",agentId);
			map.put("agentName",agentName);
            map.put("authorizationChannel",authorizationChannel);
            map.put("agentstatusTime",new Date());
            map.put("agentStatus","1");
            map.put("serviceNum","5");
            map.put("ipAddress",agentIpAddress);
            map.put("connectTime",connectTime);
			HttpResult httpResult =httpAPIService.doPost(crmAgentInfoSaveUrl,map);
			logger.info(httpResult.getBody());
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
			    logger.info(e.toString());
		    }
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
        String findAgent = socketSessionToAgentMap.get(socketKey);
        String findCustomer = socketSessionToCustomerMap.get(socketKey);

        /**
         * 如果是座席socket断开，有两种情况，座席签出，或者座席意外断线。
         * 这两种情况均需要对座席是否正在服务客户做出判断，并做相应的处理。
         */
        if(null != findAgent && StringUtils.isNotBlank(findAgent)){
            //删除socket==>座席 map
            socketSessionToAgentMap.remove(socketKey);
            logger.info("onDisconnect 座席端断开，strFindAgent="+findAgent);
            //删除座席==>socket Map
            agentToSocketMap.remove(findAgent);
            //删除座席信息map
            agentStatusMap.remove(findAgent);

            //判断该座席是否有服务的客户
            if(true == customerToAgentMap.containsValue(findAgent)){
                //如果该座席有服务的客户，将罗列所有的客户给客户发消息，同时为客户重新分配
                //1、获取该座席正在服务的客户数组
                ArrayList<String> customerArray = getCustomerArrByAgentId(customerToAgentMap, findAgent);

                //2、给这些客户发送通知消息、删除客户与座席对应信息、重新为这些客户分配座席
                for(String customerId : customerArray){
                    if(null != customerId && StringUtils.isNotBlank(customerId)) {
                        //查找客户socket准备发消息
                        SocketIOClient socketCustomer = customerToSocketMap.get(customerId);
                        if(null != socketCustomer){
                            MessageInfo msgInfo = new MessageInfo();
                            msgInfo.setMsgType(ConstElement.msgType_notice);
                            msgInfo.setMsgContent("请稍后，将为你重新分配座席");
                            socketCustomer.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
                        }

                        //删除客户与座席对应信息
                        customerToAgentMap.remove(customerId);

                        //添加到排队
                        customerQueue.add(customerId);
                        //触发分配座席
                        allocateAgent();
                    }
                }
            }
        }

        /**
         * 如果是客户端断开，可能是客户主动关闭，或者网络中断，需要做相应的处理
         */
        if(null != findCustomer && StringUtils.isNotBlank(findCustomer)) {

            // 1、删除socket与客户对应表
            socketSessionToCustomerMap.remove(socketKey);
            logger.info("onDisconnect 客户端断开，strFindCustomer="+findCustomer);
            // 2、删除客户对应socket Map
            customerToSocketMap.remove(findCustomer);
            // 3、删除客户详细表
            customerStatusMap.remove(findCustomer);

            // 4、如果客户等待队列里有该客户，删除他，因为他断开了。
            for(int i=0; i<customerQueue.size(); i++){
                if(customerQueue.get(i) == findCustomer){
                    customerQueue.remove(i);
                    i--;
                }
            }

            // 4、删除并 获取该服务该客户的座席
            String strAgentID = customerToAgentMap.remove(findCustomer);
            logger.info("找到为该客户服务的座席，strAgentID="+strAgentID);
            if(null != strAgentID && StringUtils.isNotBlank(strAgentID)) {
                // 5、找到该座席的socket，命令座席这个客户已经断开，可以结束这个座席的服务了
                SocketIOClient socketAgent = agentToSocketMap.get(strAgentID);
                if(null != socketAgent){
                    MessageInfo msgInfo = new MessageInfo();
                    msgInfo.setMsgType(ConstElement.msgType_command);
                    msgInfo.setAgentId(strAgentID);
                    msgInfo.setCustomerId(findCustomer);
                    msgInfo.setCommandType(ConstElement.commandType_toDisconnect);
                    msgInfo.setMsgContent("客户已经断开");
                    socketAgent.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
                }

                // 6、触发分配座席
                allocateAgent();
            }
        }
    }

    public ArrayList getCustomerArrByAgentId(ConcurrentMap<String, String> cusToAgentMap, String agentId){
        ArrayList<String> customerArray = new ArrayList<>();

        Iterator<ConcurrentMap.Entry<String, String>> entries = cusToAgentMap.entrySet().iterator();
        while(entries.hasNext()){
            ConcurrentMap.Entry<String, String> entry = entries.next();

            if(agentId.equals(entry.getValue())){
                customerArray.add(entry.getKey());
            }
        }

        return customerArray;
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
        socket.sendEvent(ConstElement.eventType_Notice,"客户状态更新成功");

        try {
            String customerId =customerInfo.getCustomerId();
            String customerStatus =customerInfo.getCustomerStatus();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("customerId",customerId);
            map.put("customerStatus",customerStatus);

            HttpResult httpResult =httpAPIService.doPost(crmCustomerInfoUpdateUrl,map);
            logger.info(httpResult.getBody());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.info(e.toString());
        }

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

        //从Messageinfo中获取客户ID
        String serviceId=data.getServiceId();
        String senderName=data.getSenderName();
        String customerId=data.getCustomerId();
        String customerName=data.getCustomerName();
        String msgType=data.getMsgType();
        String msgChannel=data.getMsgChannel();
        String contentType=data.getContentType();
        String msgContent=data.getMsgContent();

        if(msgType.equals(ConstElement.msgType_chat)){
            //通过源ID查询Map获取坐席ID
            if(customerId!=null&& StringUtils.isNotBlank(customerId)) {
                String agentId = customerToAgentMap.get(customerId);

                //通过坐席ID查询到坐席的socket连接对象
                if(agentId != null && StringUtils.isNotBlank(agentId)){
                    SocketIOClient agentSocketIOClient = agentToSocketMap.get(agentId);

                    if (agentSocketIOClient!=null && agentSocketIOClient.isChannelOpen()) {
                        //通过坐席socket连接对象转发该消息
                        /**
                        MessageInfo sendData = new MessageInfo();
                        sendData.setCustomerId(customerId);
                        sendData.setAgentId(agentId);
                        sendData.setMsgType(ConstElement.msgType_chat);
                        sendData.setMsgContent(data.getMsgContent());
                        sendData.setServiceId(data.getServiceId());
                        agentSocketIOClient.sendEvent(ConstElement.eventType_agentMsg, sendData);
                         */
                        agentSocketIOClient.sendEvent(ConstElement.eventType_agentMsg, data);

                        try {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("serviceId",serviceId);
                            map.put("senderName",senderName);
                            map.put("agentId",agentId);
                            map.put("customerId",customerId);
                            map.put("customerName",customerName);
                            map.put("msgType",msgType);
                            map.put("msgChannel",msgChannel);
                            map.put("contentType",contentType);
                            map.put("msgContent",msgContent);
                            map.put("createTime",new Date());
                            HttpResult httpResult =httpAPIService.doPost(crmmessageinfoSaveUrl,map);
                            logger.info(httpResult.getBody());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            logger.info(e.toString());
                        }
                    }
                }
            }
        }
        else if(msgType.equals(ConstElement.msgType_command)){
            //命令类的消息，做相应的处理
        }
        else if(msgType.equals(ConstElement.msgType_notice)){
            //处理通知类消息
        }
        else {
            //非法消息，或者不正常消息
            socket.sendEvent(ConstElement.eventType_Notice,"消息不符合规定格式协议");
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
        socket.sendEvent(ConstElement.eventType_Notice,"座席状态更新成功");

        try {
            String agentId =agentInfo.getAgentId();
            String agentStatus =agentInfo.getAgentStatus();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("agentId",agentId);
            map.put("agentStatus",agentStatus);

            HttpResult httpResult =httpAPIService.doPost(crmAgentInfoUpdateUrl,map);
            logger.info(httpResult.getBody());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }

    /**
     * 坐席端消息事件
     *
     * @param socket  　坐席端信息
     * @param data    　坐席端发送数据
     */
    @OnEvent(value = "onAgentMessageEvent")
    public void onAgentMessageEvent(SocketIOClient socket, MessageInfo data) {

        logger.info("onAgentMessageEvent 发来消息：" + data.toString());
        socket.sendEvent(ConstElement.eventType_Notice,"收到你发来的消息"+data.toString());

        //从Messageinfo中获取源ID
        String serviceId=data.getServiceId();
        String senderName=data.getSenderName();
        String agentId=data.getAgentId();
        String agentName=data.getAgentName();
        String customerId = data.getCustomerId();
        String msgType = data.getMsgType();
        String msgChannel=data.getMsgChannel();
        String contentType=data.getContentType();
        String msgContent=data.getMsgContent();


        //聊天类型的消息，需要进行转发
        if(msgType.equals(ConstElement.msgType_chat)){

            if(null != customerId && StringUtils.isNotBlank(customerId) && null != agentId && StringUtils.isNotBlank(agentId)){
                //通过客户ID查询到客户的socket连接对象
                SocketIOClient customerSocketIOClient = customerToSocketMap.get(customerId);

                if (customerSocketIOClient != null && customerSocketIOClient.isChannelOpen()) {
                    //通过客户socket连接对象转发该消息
                    /**
                    MessageInfo msgInfo = new MessageInfo();
                    msgInfo.setAgentId(agentId);
                    msgInfo.setAgentNickName(data.getAgentNickName());
                    msgInfo.setCustomerId(customerId);
                    msgInfo.setMsgType(ConstElement.msgType_chat);
                    msgInfo.setMsgContent(data.getMsgContent());
                    msgInfo.setContentType(data.getContentType());
                    msgInfo.setServiceId(data.getServiceId());
                    customerSocketIOClient.sendEvent(ConstElement.eventType_customerMsg, msgInfo);
                     */

                    customerSocketIOClient.sendEvent(ConstElement.eventType_customerMsg, data);

                    try {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("serviceId",serviceId);
                        map.put("senderName",senderName);
                        map.put("agentId",agentId);
                        map.put("agentName",agentName);
                        map.put("customerId",customerId);
                        map.put("msgType",msgType);
                        map.put("msgChannel",msgChannel);
                        map.put("contentType",contentType);
                        map.put("msgContent",msgContent);
                        map.put("createTime",new Date());
                        HttpResult httpResult =httpAPIService.doPost(crmmessageinfoSaveUrl,map);
                        logger.info(httpResult.getBody());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        logger.info(e.toString());
                    }
                }
            }
            else{
                socket.sendEvent(ConstElement.eventType_Notice,"服务器：座席ID或者客户ID为空，数据不完整");
            }
        }
        //命令类型的消息，针对命令定义的action进行相应的操作
        else if(msgType.equals(ConstElement.msgType_command)){

        }
        //通知类的消息，做相应的处理
        else if(msgType.equals(ConstElement.msgType_notice)){

        }
        else{
            //非法消息，或者不正常消息
            socket.sendEvent(ConstElement.eventType_Notice,"消息不符合格式协议:"+data.getMsgContent());
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

        AcdUtils acdUtils =new AcdUtils();
        Iterator<String> it = customerQueue.iterator();
        while(it.hasNext()){
            String customerId = it.next();
            String allocAgentId = acdUtils.distribution(agentStatusMap);

            if(allocAgentId!=null&&StringUtils.isNotBlank(allocAgentId)) {
                //建立客户与坐席的对应表Map
                customerToAgentMap.put(customerId, allocAgentId);
                logger.info("customerId="+customerId+ "分配的座席ID="+allocAgentId);
                logger.info("customerToAgentMap"+customerToAgentMap.toString());

                //产生服务id
                String serviceId=UUID.randomUUID().toString().replace("-","");

                //给座席发命令消息服务新的客户
                MessageInfo msgInfoAgent = new MessageInfo();
                msgInfoAgent.setServiceId(serviceId);
                msgInfoAgent.setAgentId(allocAgentId);
                msgInfoAgent.setCustomerId(customerId);
                msgInfoAgent.setCommandType(ConstElement.commandType_toAgent);
                msgInfoAgent.setMsgType(ConstElement.msgType_command);
                msgInfoAgent.setMsgContent("为你分配了新的客户,客户ID="+customerId);
                SocketIOClient socketAgent = agentToSocketMap.get(allocAgentId);
                socketAgent.sendEvent(ConstElement.eventType_agentMsg,msgInfoAgent);

                //给客户发通知消息
                MessageInfo msgInfoCus = new MessageInfo();
                msgInfoCus.setServiceId(serviceId);
                msgInfoCus.setCustomerId(customerId);
                msgInfoCus.setAgentId(allocAgentId);
                msgInfoCus.setMsgType(ConstElement.msgType_notice);
                msgInfoCus.setMsgContent("已经为您分配座席，坐席ID="+allocAgentId);
                SocketIOClient socketCustomer = customerToSocketMap.get(customerId);
                socketCustomer.sendEvent(ConstElement.eventType_customerMsg,msgInfoCus);

                it.remove();
            }
            else {
                logger.info("没有空闲坐席");
            }
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
