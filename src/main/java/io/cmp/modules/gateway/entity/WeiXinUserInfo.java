package io.cmp.modules.gateway.entity;

import lombok.Data;

@Data
public class WeiXinUserInfo {
    private String openid;
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;
    private String privilege;
    private String unionid;
}
