package io.cmp.modules.gateway.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.cmp.common.utils.HttpResult;
import io.cmp.modules.gateway.entity.AgentInfo;
import io.cmp.modules.gateway.entity.AnswerContent;
import io.cmp.modules.gateway.entity.CustomerInfo;
import io.cmp.modules.gateway.utils.AcdUtils;
import io.cmp.modules.gateway.utils.ConstElement;
import io.cmp.modules.gateway.utils.NlpUtils;
import io.cmp.modules.sys.service.HttpAPIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.Socket;
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

    @Autowired
    private NlpUtils nlpUtils; //nlp处理

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

    @Value("${httpclient.crmagentcustomerserviceSaveUrl}")
    private String crmagentcustomerserviceSaveUrl;

    @Value("${robot.channel}")
    private String robotChannel;
    @Value("${robot.city}")
    private String robotCity;
    @Value("${robot.business}")
    private String robotBusiness;

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
            logger.info("onConnect 客户注册：customerToSocketMap = " + customerToSocketMap.toString());
            customerToSocketMap.put(customerId, socket);
            logger.info("onConnect 客户注册-注册后：customerToSocketMap = " + customerToSocketMap.toString());
            //存储客户状态对象

            logger.info("onConnect 客户注册：customerStatusMap = " + customerStatusMap.toString());
            customerStatusMap.put(customerId,customerInfo);
            logger.info("onConnect 客户注册-注册后：customerStatusMap = " + customerStatusMap.toString());
            //存储socket对象对应的客户ID

            String socketCustomerKey = socket.getSessionId() + socket.getRemoteAddress().toString();

            logger.info("onConnect 客户注册：socketSessionToCustomerMap = " + socketSessionToCustomerMap.toString());
            socketSessionToCustomerMap.put(socketCustomerKey,customerId);
            logger.info("onConnect 客户注册-注册后：socketSessionToCustomerMap = " + socketSessionToCustomerMap.toString());

            logger.info("客户端已连接 customerId=" + customerId+",customerName=" + customerName+",客户访问渠道=" + accessChannel+",客户ip地址=" + customerIpAddress+",接入时间="+connectTime);

            /**
             * 判断服务模式是机器人还是真人，做不同的处理
             * 如果serviceMode是person，转人工处理，为空或者chatRobot均为机器人服务
             */
            //获取服务模式
            String serviceMode = socket.getHandshakeData().getSingleUrlParam("serviceMode");
            MessageInfo noticeInfo = new MessageInfo();
            if(null != serviceMode && ConstElement.serviceMode_person.equals(serviceMode)){
                noticeInfo.setMsgType(ConstElement.msgType_notice);
                noticeInfo.setSenderName(ConstElement.senderName_server);
                noticeInfo.setServiceMode(ConstElement.serviceMode_person);
                noticeInfo.setNoticeContent("已经建立连接，正在为您分配座席，请稍等……");
                socket.sendEvent(ConstElement.eventType_notice, noticeInfo);
                //将客户ID添加到待分配队列
                addCustomerQueue(customerId);
                //调用分配算法分配
                allocateAgent();
            }
            else{
                noticeInfo.setMsgType(ConstElement.msgType_notice);
                noticeInfo.setServiceMode(ConstElement.serviceMode_chatRobot);
                noticeInfo.setSenderName(ConstElement.senderName_server);
                noticeInfo.setServiceId(UUID.randomUUID().toString().replace("-",""));
                noticeInfo.setNoticeContent("机器人小软为您服务，请输入您要咨询的问题……");
                socket.sendEvent(ConstElement.eventType_notice, noticeInfo);
            }


            //存储数据
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
                HttpResult httpResult =httpAPIService.doPostJson(crmCustomerInfoSaveUrl,map);
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
            logger.info("onConnect 座席注册：agentToSocketMap = " + agentToSocketMap.toString());
            agentToSocketMap.put(agentId, socket);
            logger.info("onConnect 座席注册-注册后：agentToSocketMap = " + agentToSocketMap.toString());
            //存储坐席状态对象
            logger.info("onConnect 座席注册：agentStatusMap = " + agentStatusMap.toString());
            agentStatusMap.put(agentId,agentInfo);
            logger.info("onConnect 座席注册-注册后：agentStatusMap = " + agentStatusMap.toString());
            //存储socket对座席id
            logger.info("onConnect 座席注册：socketSessionToAgentMap = " + socketSessionToAgentMap.toString());
            String socketAgentKey = socket.getSessionId() + socket.getRemoteAddress().toString();
            socketSessionToAgentMap.put(socketAgentKey,agentId);
            logger.info("onConnect 座席注册-注册后：socketSessionToAgentMap = " + socketSessionToAgentMap.toString());

            //回发消息,通过定义好的方式进行
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setMsgType(ConstElement.msgType_notice);
            msgInfo.setSenderName(ConstElement.senderName_server);
            msgInfo.setNoticeContent("座席端已经建立连接，开始服务");
            //回发消息
            socket.sendEvent(ConstElement.eventType_notice, msgInfo);
            logger.info("坐席端已连接 agentId=" + agentId+",agentName=" + agentName+",坐席授权渠道=" + authorizationChannel+",坐席ip地址=" + agentIpAddress+",接入时间="+connectTime);

            //调用分配算法分配
            allocateAgent();

            //存储数据
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
			HttpResult httpResult =httpAPIService.doPostJson(crmAgentInfoSaveUrl,map);
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
            socket.sendEvent(ConstElement.eventType_info,"你是非法注册用户");
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

        /**
         * 无论如何这个socke删除了,socket to agent 和 customer的map表中的键值对是一定要删除的；
         * 通过remove方法删除并返回对应的值，是最优处理方法
         */
        String findAgent = socketSessionToAgentMap.remove(socketKey);
        String findCustomer = socketSessionToCustomerMap.remove(socketKey);

        /**
         * 如果是座席socket断开，有两种情况，座席签出，或者座席意外断线。
         * 这两种情况均需要对座席是否正在服务客户做出判断，并做相应的处理。
         */
        socketSessionToAgentMap.remove(socketKey);
        if(null != findAgent && StringUtils.isNotBlank(findAgent)){
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
                        //删除客户与座席对应信息
                        customerToAgentMap.remove(customerId);
                        //查找客户socket准备发消息
                        SocketIOClient socketCustomer = customerToSocketMap.get(customerId);
                        if(null != socketCustomer){
                            MessageInfo msgInfo = new MessageInfo();
                            msgInfo.setMsgType(ConstElement.msgType_notice);
                            msgInfo.setCustomerId(customerId);
                            msgInfo.setSenderName(ConstElement.senderName_server);
                            msgInfo.setNoticeContent(getAllocateNoticeMessage());
                            msgInfo.setServiceMode(ConstElement.serviceMode_person);
                            socketCustomer.sendEvent(ConstElement.eventType_notice, msgInfo);

                            //添加到排队
                            addCustomerQueue(customerId);
                            //触发分配座席
                            allocateAgent();
                        }
                    }
                }
            }
        }

        /**
         * 如果是客户端断开，可能是客户主动关闭，或者网络中断，需要做相应的处理
         */
        if(null != findCustomer && StringUtils.isNotBlank(findCustomer)) {
            logger.info("onDisconnect 客户端断开，strFindCustomer="+findCustomer);
            // 2、删除客户对应socket Map
            customerToSocketMap.remove(findCustomer);
            // 3、删除客户详细表
            customerStatusMap.remove(findCustomer);

            // 4、如果客户等待队列里有该客户，删除他，因为他断开了。
            logger.info("onDisconnect 客户断开 customerQueue = " + customerQueue.toString());
            customerQueue.remove(findCustomer);
            /**
            for(int i=0; i<customerQueue.size(); i++){
                String strQueue = customerQueue.get(i);
                if(findCustomer.equals(strQueue)){
                    customerQueue.remove(i);
                    i--;
                }
            }
             */
            logger.info("onDisconnect 客户断开 customerQueue 处理后 = " + customerQueue.toString());

            // 5、删除并获取该服务该客户的座席，如果是机器人座席，依然执行这一步，会找不到人工座席，不影响程序执行。
            String strAgentID = customerToAgentMap.remove(findCustomer);
            logger.info("找到为该客户服务的座席，strAgentID="+strAgentID);
            if(null != strAgentID && StringUtils.isNotBlank(strAgentID)) {
                // 5、找到该座席的socket，命令座席这个客户已经断开，可以结束这个座席的服务了
                SocketIOClient socketAgent = agentToSocketMap.get(strAgentID);
                if(null != socketAgent){
                    MessageInfo msgInfo = new MessageInfo();
                    msgInfo.setMsgType(ConstElement.msgType_notice);
                    msgInfo.setSenderName(ConstElement.senderName_server);
                    msgInfo.setAgentId(strAgentID);
                    msgInfo.setCustomerId(findCustomer);
                    msgInfo.setNoticeContent("客户已经断开……");
                    socketAgent.sendEvent(ConstElement.eventType_notice, msgInfo);

                    //客户已经断开，该坐席的当前服务结束，释放一个服务客户资源，需要处理后续内容，与分配相关。
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

    private String getAllocateNoticeMessage(){
        int queueSize = customerQueue.size();

        String noticeMessage = "正在为您分配人工座席，目前有" + String.valueOf(queueSize) + "个客户等待，";
        if(queueSize <= 1){
            noticeMessage = noticeMessage + "将很快为您接通，请稍后……";
        }
        else{
            noticeMessage = noticeMessage + "预计等待" + String.valueOf(queueSize*1.5) + "分钟，请您耐心等待，谢谢……";
        }

        return noticeMessage;
    }

    /**
     * 同socket和Messageinfo的数据进行分配座席
     * 该data对象中的customerId值不能为空
     * @param socket
     * @param data
     */
    private void toAllocateBySocketAndData(SocketIOClient socket, MessageInfo data){
        if(null == socket || null == data){
            return;
        }

        // 1、获取客户id
        String customerId = data.getCustomerId();
        if(null == customerId || StringUtils.isBlank(customerId)){
            return;
        }

        // 2、添加到排队队列
        addCustomerQueue(customerId);

        // 3、发送提示消息
        data.setSenderName(ConstElement.senderName_server);
        data.setNoticeContent(getAllocateNoticeMessage());
        data.setMsgType(ConstElement.msgType_notice);
        socket.sendEvent(ConstElement.eventType_notice,data);

        // 4、分配人工座席
        allocateAgent();
    }

    private boolean isRelatedWithAgentBetweenCustomer(String agentId, String customerId){
        if(null == agentId || StringUtils.isBlank(agentId) || null == customerId || StringUtils.isBlank(customerId)){
            return false;
        }

        //判断座席和客户是否有关联关系
        String findAgentId = customerToAgentMap.get(customerId);
        if(null == findAgentId || StringUtils.isBlank(findAgentId)){
            return false;
        }
        else if(findAgentId.equals(agentId)){
            return true;
        }
        else{
            return false;
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
        socket.sendEvent(ConstElement.eventType_info,"客户状态更新成功");

        try {
            String customerId =customerInfo.getCustomerId();
            String customerStatus =customerInfo.getCustomerStatus();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("customerId",customerId);
            map.put("customerStatus",customerStatus);

            HttpResult httpResult =httpAPIService.doPostJson(crmCustomerInfoUpdateUrl,map);
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
    public void onCustomerMessageEvent(SocketIOClient socket, MessageInfo data, AckRequest ackRequest) {
        logger.info("onCustomerMessageEvent 发来消息：" + data.toString());
        socket.sendEvent(ConstElement.eventType_info,"收到你发来的消息"+data.toString());

        //从Messageinfo中获取客户ID
        String serviceId=data.getServiceId();
        String senderName=data.getSenderName();
        String customerId=data.getCustomerId();
        String customerName=data.getCustomerName();
        String msgType=data.getMsgType();
        String msgChannel=data.getMsgChannel();
        String contentType=data.getContentType();
        String msgContent=data.getMsgContent();

        String serviceMode = data.getServiceMode();
        String agentId = "";

        //客户发来的消息，首先要保证customerID不能没有
        if(null == customerId || StringUtils.isBlank(customerId)){
            socket.sendEvent(ConstElement.eventType_info,"onCustomerMessageEvent事件消息，customerId为空，服务器无法处理");
            return;
        }

        if(msgType.equals(ConstElement.msgType_chat)){
            /**
             * 判断服务模式是机器人还是人工
             */
            //人工模式
            if(null != serviceMode && ConstElement.serviceMode_person.equals(serviceMode)){
                //通过源ID查询Map获取坐席Id
                agentId = customerToAgentMap.get(customerId);
                logger.info("通过customerId查询Map获取坐席ID agentId="+agentId);

                //通过坐席ID查询到坐席的socket连接对象，进行消息转发
                if(agentId != null && StringUtils.isNotBlank(agentId)){
                    SocketIOClient agentSocketIOClient = agentToSocketMap.get(agentId);
                    if (agentSocketIOClient != null) {
                        //通过坐席socket连接对象转发该消息
                        data.setServiceMode(ConstElement.serviceMode_person);
                        agentSocketIOClient.sendEvent(ConstElement.eventType_agentMsg, data);
                    }
                }
                else{ //如果没有找到客户座席对应表需要重新分配人工座席
                    toAllocateBySocketAndData(socket,data);
                }
            }
            else{ //机器人聊天模式
                chatWithRobot(socket,data);
            }

            //数据存储
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("serviceId",serviceId);
                map.put("senderName",senderName);
                map.put("agentCode",agentId);
                map.put("customerId",customerId);
                map.put("customerName",customerName);
                map.put("msgType",msgType);
                map.put("msgChannel",msgChannel);
                map.put("contentType",contentType);
                map.put("msgContent",msgContent);
                map.put("createTime",new Date());
                HttpResult httpResult =httpAPIService.doPostJson(crmmessageinfoSaveUrl,map);
                logger.info(httpResult.getBody());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                logger.info(e.toString());
            }


        }
        else if(msgType.equals(ConstElement.msgType_command)){
            //命令类的消息，做相应的处理

            String commandType = data.getCommandType();
            if(null != commandType && ConstElement.commandType_toAgent.equals(commandType)){
                // 1、查询是否有座席为他服务，如果有不予理会
                if(!customerToAgentMap.containsKey(customerId)){
                    toAllocateBySocketAndData(socket,data);
                }
                else{
                    data.setServiceMode(ConstElement.serviceMode_person);
                    data.setSenderName(ConstElement.senderName_server);
                    data.setMsgType(ConstElement.msgType_notice);
                    data.setNoticeContent("人工座席正在为您服务……");
                    socket.sendEvent(ConstElement.eventType_notice,data);
                }

            }
            else if(null != commandType && ConstElement.commandType_toRobot.equals(commandType)){
                //客户端发起转机器人操作步骤
                // 1、如果客户等待队列有该客户，删除他，因为已经不需要人工座席服务了。
                customerQueue.remove(customerId);
                // 2、查找是否有座席为他服务，如果有取消他的服务，给座席端发消息
                String strAgentID = customerToAgentMap.remove(customerId);
                logger.info("找到为该客户服务的座席，准备断开，strAgentID="+strAgentID);
                if(null != strAgentID && StringUtils.isNotBlank(strAgentID)) {
                    SocketIOClient socketAgent = agentToSocketMap.get(strAgentID);
                    if (null != socketAgent) {
                        /**
                        data.setMsgType(ConstElement.msgType_notice);
                        data.setSenderName(ConstElement.senderName_server);
                        data.setAgentId(strAgentID);
                        data.setNoticeContent("客户转为智能服务，该人工服务结束");
                        */

                        MessageInfo noticeMsg = new MessageInfo();
                        noticeMsg.setMsgType(ConstElement.msgType_notice);
                        noticeMsg.setSenderName(ConstElement.senderName_server);
                        noticeMsg.setAgentId(strAgentID);
                        noticeMsg.setCustomerId(data.getCustomerId());
                        noticeMsg.setServiceId(data.getServiceId());
                        noticeMsg.setServiceMode(data.getServiceMode());
                        noticeMsg.setNoticeContent("客户转为智能服务，该人工服务结束");
                        socketAgent.sendEvent(ConstElement.eventType_notice, noticeMsg);

                        //客户已经断开，该坐席的当前服务结束，释放一个服务客户资源，需要处理后续内容，与分配相关。
                    }
                }

                // 3、改变服务模式为机器人服务，发送提示消息
                data.setServiceMode(ConstElement.serviceMode_chatRobot);
                data.setSenderName(ConstElement.senderName_server);
                data.setMsgType(ConstElement.msgType_notice);
                data.setNoticeContent("智能客服小软为您服务，请输入您要咨询的问题……");
                socket.sendEvent(ConstElement.eventType_notice,data);
            }
            else if(null != commandType && ConstElement.commandType_toDisconnect.equals(commandType)){
                //收到客户端断开消息
            }
            else{
                socket.sendEvent(ConstElement.eventType_info,"未知命令，请联系管理员");
            }

        }
        else if(msgType.equals(ConstElement.msgType_notice)){
            //处理通知类消息
        }
        else {
            //非法消息，或者不正常消息
            socket.sendEvent(ConstElement.eventType_info,"消息不符合规定格式协议");
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
        socket.sendEvent(ConstElement.eventType_info,"座席状态更新成功");

        try {
            String agentId =agentInfo.getAgentId();
            String agentStatus =agentInfo.getAgentStatus();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("agentId",agentId);
            map.put("agentStatus",agentStatus);

            HttpResult httpResult =httpAPIService.doPostJson(crmAgentInfoUpdateUrl,map);
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
    public void onAgentMessageEvent(SocketIOClient socket, MessageInfo data, AckRequest ackRequest) {

        logger.info("onAgentMessageEvent 发来消息：" + data.toString());
        socket.sendEvent(ConstElement.eventType_notice,"收到你发来的消息"+data.toString());

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
            //判断座席是否关联该客户，如果已经没有关联，提示座席该客户已经结束服务，消息不再转发
            if(!isRelatedWithAgentBetweenCustomer(agentId,customerId)){
                //给座席端发送提示信息
                data.setMsgType(ConstElement.msgType_notice);
                data.setSenderName(ConstElement.senderName_server);
                data.setNoticeContent("客户已经断开，请结束该客户服务……");
                socket.sendEvent(ConstElement.eventType_notice,data);
            }
            else {
                if(null != customerId && StringUtils.isNotBlank(customerId) && null != agentId && StringUtils.isNotBlank(agentId)){
                    //通过客户ID查询到客户的socket连接对象
                    SocketIOClient customerSocketIOClient = customerToSocketMap.get(customerId);

                    if (customerSocketIOClient != null) {
                        //通过客户socket连接对象转发该消息
                        customerSocketIOClient.sendEvent(ConstElement.eventType_customerMsg, data);

                        try {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("serviceId",serviceId);
                            map.put("senderName",senderName);
                            map.put("agentCode",agentId);
                            map.put("agentName",agentName);
                            map.put("customerId",customerId);
                            map.put("msgType",msgType);
                            map.put("msgChannel",msgChannel);
                            map.put("contentType",contentType);
                            map.put("msgContent",msgContent);
                            map.put("createTime",new Date());
                            HttpResult httpResult =httpAPIService.doPostJson(crmmessageinfoSaveUrl,map);
                            logger.info(httpResult.getBody());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            logger.info(e.toString());
                        }
                    }
                    else{
                        // 通过customerToSocketMap没有找到客户socket信息
                        // 有可能客户意外断开，服务器没有收到断开消息，也有可能是其他，这个时候解除对应关系；
                        // 删除该客户的所有信息
                        customerToAgentMap.remove(customerId);
                        customerStatusMap.remove(customerId);
                        customerToSocketMap.remove(customerId);
                        customerQueue.remove(customerId);
                        //删除该sockeid对应客户id的map中的值
                        Iterator<ConcurrentHashMap.Entry<String, String>> entries = socketSessionToCustomerMap.entrySet().iterator();
                        while(entries.hasNext()){
                            ConcurrentHashMap.Entry<String, String> entry = entries.next();
                            if(customerId.equals(entry.getValue())){
                                entries.remove();
                            }
                        }

                        //给座席端发送提示信息
                        data.setMsgType(ConstElement.msgType_notice);
                        data.setSenderName(ConstElement.senderName_server);
                        data.setNoticeContent("客户已经断开，请结束该服务");
                        socket.sendEvent(ConstElement.eventType_notice,data);
                    }
                }
                else{
                    socket.sendEvent(ConstElement.eventType_info,"服务器：座席ID或者客户ID为空，数据不完整");
                }
            }
        }
        //命令类型的消息，针对命令定义的action进行相应的操作
        else if(msgType.equals(ConstElement.msgType_command)){
            String commandType = data.getCommandType();
            if(null != commandType && ConstElement.commandType_toDisconnect.equals(commandType)){
                //座席端主动结束服务，如果客户没有断开，将客户转为智能客服
                if(null != customerId && StringUtils.isNotBlank(customerId)){
                    SocketIOClient socketCustomer = customerToSocketMap.get(customerId);
                    if(null != socketCustomer){
                        // 1、给座席端发送一个提示消息
                        data.setMsgType(ConstElement.msgType_notice);
                        data.setNoticeContent("已经结束当前客户的服务");
                        data.setSenderName(ConstElement.senderName_server);
                        socket.sendEvent(ConstElement.eventType_notice,data);

                        // 2、给客户端发送一条提示消息
                        data.setMsgType(ConstElement.msgType_notice);
                        data.setNoticeContent("已经结束本次人工服务，并转为智能客服……");
                        data.setSenderName(ConstElement.senderName_server);
                        data.setServiceMode(ConstElement.serviceMode_chatRobot);
                        socketCustomer.sendEvent(ConstElement.eventType_notice,data);

                        // 3、解除客户与座席对应关系
                        customerToAgentMap.remove(customerId);
                        customerQueue.remove(customerId);
                    }
                }
            }
            else
            {
                //非法消息，或者不正常消息
                socket.sendEvent(ConstElement.eventType_info,"onAgentMessageEvent 消息不符合格式协议:"+data.getMsgContent());
            }
        }
        //通知类的消息，做相应的处理
        else if(msgType.equals(ConstElement.msgType_notice)){
            //客户发来的notice消息无需处理
            socket.sendEvent(ConstElement.eventType_info,"你一个座席给服务器发什么Notice消息");
        }
        else{
            //非法消息，或者不正常消息
            socket.sendEvent(ConstElement.eventType_info,"onAgentMessageEvent 消息不符合格式协议:"+data.getMsgContent());
        }
    }

    /**
     *
     */
    private void allocateAgent(){
        logger.info("客户队列里有"+customerQueue.size()+"个客户等待，客户队列内容" + customerQueue.toString());
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
                msgInfoAgent.setMsgChannel(ConstElement.channel_webchat);
                msgInfoAgent.setServiceMode(ConstElement.serviceMode_person);
                SocketIOClient socketAgent = agentToSocketMap.get(allocAgentId);
                if(null != socketAgent){
                    socketAgent.sendEvent(ConstElement.eventType_agentMsg,msgInfoAgent);
                }

                //给客户发通知消息
                MessageInfo msgInfoCus = new MessageInfo();
                msgInfoCus.setServiceId(serviceId);
                msgInfoCus.setCustomerId(customerId);
                msgInfoCus.setAgentId(allocAgentId);
                msgInfoCus.setMsgType(ConstElement.msgType_notice);
                msgInfoCus.setNoticeContent("已经为您分配座席，坐席ID="+allocAgentId);
                msgInfoCus.setServiceMode(ConstElement.serviceMode_person);
                msgInfoCus.setMsgChannel(ConstElement.channel_webchat);
                SocketIOClient socketCustomer = customerToSocketMap.get(customerId);
                if(null != socketCustomer){
                    socketCustomer.sendEvent(ConstElement.eventType_notice,msgInfoCus);
                }

                it.remove();

                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("serviceId",serviceId);
                    map.put("agentCode",allocAgentId);
                    map.put("customerId",customerId);
                    map.put("createTime",new Date());
                    HttpResult httpResult =httpAPIService.doPostJson(crmagentcustomerserviceSaveUrl,map);
                    logger.info(httpResult.getBody());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.info(e.toString());
                }
            }
            else {
                logger.info("没有空闲坐席");
            }
        }
    }


    /**
     * 机器人聊天
     */
    public void chatWithRobot(SocketIOClient socket, MessageInfo data){
        AnswerContent answerContent = nlpUtils.nlpSaaSQA(data.getServiceId(),data.getMsgContent(),robotChannel,robotCity,robotBusiness);
        if(null != answerContent){
            data.setAnswerContent(answerContent);
            data.setSenderName(ConstElement.senderName_chatRobot);
            data.setServiceMode(ConstElement.serviceMode_chatRobot);
            socket.sendEvent(ConstElement.eventType_customerMsg,data);
        }
        else{
            data.setMsgType(ConstElement.msgType_notice);
            data.setServiceMode(ConstElement.serviceMode_chatRobot);
            data.setSenderName(ConstElement.senderName_server);
            data.setNoticeContent("机器人繁忙中，请稍后或者转人工坐席服务");
            socket.sendEvent(ConstElement.eventType_notice,data);
        }
    }

    //添加到客户队列
    private void addCustomerQueue(String customerId){
        if(false == customerQueue.contains(customerId)){
            customerQueue.add(customerId);
        }
    }

    /**
     * 广播消息-无用信息
     */
    public void sendBroadcast() {
        for (SocketIOClient client : agentToSocketMap.values()) {
            if (client.isChannelOpen()) {
                client.sendEvent("Broadcast", "当前时间", System.currentTimeMillis());
            }
        }

    }
}
