package io.cmp.modules.gateway.entity;

import lombok.Data;

@Data
public class WeiXinAccessToken {
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
}
