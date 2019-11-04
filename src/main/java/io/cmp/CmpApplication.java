package io.cmp;

import com.corundumstudio.socketio.SocketIOServer;
import io.cmp.modules.gateway.controller.WeixinGetTokenController;
import io.cmp.modules.gateway.utils.NlpUtils;
import io.cmp.modules.gateway.utils.WeixinEnvironment;
import io.cmp.modules.sys.service.HttpAPIService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SpringBootApplication
public class CmpApplication implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(getClass());



	private static HttpAPIService httpAPIService;

	@Autowired
	public void setHttpAPIService(HttpAPIService httpAPIService)
	{
		CmpApplication.httpAPIService=httpAPIService;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context=SpringApplication.run(CmpApplication.class, args);
		String weixinGetTokenControllerUrl=context.getBean(WeixinEnvironment.class).getWeixinGetTokenControllerUrl();
		CmpApplication cmpApplication = new CmpApplication();
		cmpApplication.weixinMonitor(weixinGetTokenControllerUrl);
	}

	@Autowired
	private SocketIOServer socketIOServer;

	@Override
	public void run(String... strings) {
		socketIOServer.start();
		logger.info("socket.io启动成功！");
	}

	public void weixinMonitor(String weixinGetTokenControllerUrl) {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						ConcurrentMap<String, String> appidToSecretMap= WeixinGetTokenController.appidToSecretMap;
						ConcurrentMap<String, String> appidToAccessTokenMap= WeixinGetTokenController.appidToAccessTokenMap;
						ConcurrentMap<String, String> appidToTicketMap= WeixinGetTokenController.appidToTicketMap;

						if(appidToSecretMap!=null) {
							Iterator<String> keys = appidToSecretMap.keySet().iterator();
							while (keys.hasNext()) {
								String key = keys.next();
								String value = appidToSecretMap.get(key);
								logger.info("appid = " + key + ";secret = " + value);

								if(appidToAccessTokenMap!=null) {
									logger.info("移除appidToAccessTokenMap="+key);

									logger.info("移除前appidToAccessTokenMap="+appidToAccessTokenMap.get(key));
									//先移除appidToAccessTokenMap值
									appidToAccessTokenMap.remove(key);
									logger.info("移除后appidToAccessTokenMap="+appidToAccessTokenMap.get(key));

									logger.info("移除前appidToTicketMap="+appidToTicketMap.get(key));
									appidToTicketMap.remove(key);
									logger.info("移除后appidToTicketMap="+appidToTicketMap.get(key));

									try {
										String result = null;
										HashMap params = new HashMap();
										params.put("appid", key);
										params.put("secret", value);
										logger.info("weixinGetTokenControllerUrl="+weixinGetTokenControllerUrl);
										logger.info("httpAPIService="+httpAPIService);
										result=httpAPIService.doGet(weixinGetTokenControllerUrl,params);
										logger.info("result="+result);
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}



									logger.info("=============================================================================================================================");
								}
							}
							//10M秒刷新
							//Thread.sleep(10000);

							//1小时
							Thread.sleep(3600000);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}.start();
	}

}