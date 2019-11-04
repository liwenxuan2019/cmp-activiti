package io.cmp.modules.gateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;

@SpringBootConfiguration
public class WeixinEnvironment {
    @Value("${httpclient.weixinGetTokenControllerUrl}")
    private String weixinGetTokenControllerUrl;

    public String getWeixinGetTokenControllerUrl() {
        return weixinGetTokenControllerUrl;
    }

    public void setWeixinGetTokenControllerUrl(String weixinGetTokenControllerUrl) {
        this.weixinGetTokenControllerUrl = weixinGetTokenControllerUrl;
    }
}
