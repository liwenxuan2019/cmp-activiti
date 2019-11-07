package io.cmp.modules.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import io.cmp.modules.gateway.entity.WeiXinAccessToken;
import io.cmp.modules.gateway.entity.WeiXinUserInfo;
import io.cmp.modules.sys.service.HttpAPIService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/weixin")
public class WeixinGetUserInfoController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HttpAPIService httpAPIService;

    @Value("${httpclient.weixinGetAccessTokenUrl}")
    private String weixinGetAccessTokenUrl;

    @Value("${httpclient.weixinGetUserInfoUrl}")
    private String weixinGetUserInfoUrl;


    //微信appid对应code Map
    public static ConcurrentMap<String, String> appidToCodeMap= new ConcurrentHashMap<>();

    //微信appid对应accessToken Map
    public static ConcurrentMap<String, WeiXinAccessToken> appidToAccessTokenMap= new ConcurrentHashMap<>();

    //微信appid对应ticket Map
    public static ConcurrentMap<String, WeiXinUserInfo> appidToUserInfoMap= new ConcurrentHashMap<>();


    @GetMapping("/getUserInfo")
    public String getUserInfo(@RequestParam Map<String, Object> params) {
        String appid= (String)params.get("appid");
        String secret=(String)params.get("secret");
        String code=(String)params.get("code");
        logger.info("appid="+appid);
        logger.info("secret="+secret);
        logger.info("code="+code);

        String result= null;
        String resultUserInfo= null;
        WeiXinAccessToken weiXinAccessTokenTemp=appidToAccessTokenMap.get(appid);
        WeiXinUserInfo weiXinUserInfoTemp=appidToUserInfoMap.get(appid);
        logger.info("weiXinAccessTokenTemp="+weiXinAccessTokenTemp);
        logger.info("weiXinUserInfoTemp="+weiXinUserInfoTemp);

        if(null!=weiXinAccessTokenTemp) {
            String access_token=weiXinAccessTokenTemp.getRefresh_token();
            String refresh_token=weiXinAccessTokenTemp.getRefresh_token();
            String openid=weiXinAccessTokenTemp.getOpenid();
            String scope=weiXinAccessTokenTemp.getScope();
            HashMap resultMap=new HashMap();
            resultMap.put("access_token",access_token);
            resultMap.put("expires_in",7200);
            resultMap.put("refresh_token",refresh_token);
            resultMap.put("openid",openid);
            resultMap.put("scope",scope);
            result =JSONObject.toJSONString(resultMap);
            logger.info("缓存中获取result="+result);

            if(null!=weiXinUserInfoTemp) {
                String nickname=weiXinUserInfoTemp.getNickname();
                String sex=weiXinUserInfoTemp.getSex();
                String province=weiXinUserInfoTemp.getProvince();
                String city=weiXinUserInfoTemp.getCity();
                String country=weiXinUserInfoTemp.getCountry();
                String headimgurl=weiXinUserInfoTemp.getHeadimgurl();
                String privilege=weiXinUserInfoTemp.getPrivilege();
                String unionid=weiXinUserInfoTemp.getUnionid();

                HashMap resultUserInfoMap=new HashMap();
                resultUserInfoMap.put("openid",openid);
                resultUserInfoMap.put("nickname",nickname);
                resultUserInfoMap.put("sex",sex);
                resultUserInfoMap.put("province",province);
                resultUserInfoMap.put("city",city);
                resultUserInfoMap.put("country",country);
                resultUserInfoMap.put("headimgurl",headimgurl);
                resultUserInfoMap.put("privilege",privilege);
                resultUserInfoMap.put("unionid",unionid);

                resultUserInfo =JSONObject.toJSONString(resultUserInfoMap);
                logger.info("缓存中获取resultUserInfot="+resultUserInfo);

            }
        }
        else
        {
            try {
                result = httpAPIService.doGet(weixinGetAccessTokenUrl, params);
                if (result != null && StringUtils.isNotBlank(result)) {
                    logger.info("接口中获取result=" + result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String access_token = jsonObject.getString("access_token");
                    logger.info("access_token=" + access_token);
                    String openid = jsonObject.getString("openid");
                    logger.info("openid=" + openid);

                    if(access_token!=null && StringUtils.isNotBlank(access_token))
                    {
                        WeiXinAccessToken weiXinAccessToken = new WeiXinAccessToken();
                        weiXinAccessToken.setAccess_token(access_token);
                        weiXinAccessToken.setExpires_in(7200);
                        weiXinAccessToken.setRefresh_token("REFRESH_TOKEN");
                        weiXinAccessToken.setOpenid(openid);
                        weiXinAccessToken.setScope("SCOPE");
                        appidToAccessTokenMap.put(appid, weiXinAccessToken);


                        String appidToCodeMapTemp=appidToCodeMap.get(appid);
                        logger.info("appidToCodeMapTemp="+appidToCodeMapTemp);
                        if(appidToCodeMapTemp==null&&StringUtils.isBlank(appidToCodeMapTemp)) {
                            appidToCodeMap.put(appid, code);
                            logger.info("code="+appidToCodeMap.get(appid));

                        }


                        Map<String, Object> paramsToken = new HashMap<String, Object>();
                        paramsToken.put("access_token", access_token);
                        paramsToken.put("openid", openid);
                        resultUserInfo = httpAPIService.doGet(weixinGetUserInfoUrl, paramsToken);

                        if (resultUserInfo != null && StringUtils.isNotBlank(resultUserInfo)) {
                            logger.info("接口中获取resultUserInfo=" + resultUserInfo);
                            JSONObject jsonObjectTicket = JSONObject.parseObject(resultUserInfo);
                            String nickname = jsonObjectTicket.getString("nickname");
                            logger.info("nickname=" + nickname);
                            String sex = jsonObjectTicket.getString("sex");
                            logger.info("sex=" + sex);
                            String province = jsonObjectTicket.getString("province");
                            logger.info("province=" + province);
                            String city = jsonObjectTicket.getString("city");
                            logger.info("city=" + city);
                            String country = jsonObjectTicket.getString("country");
                            logger.info("country=" + country);
                            String headimgurl = jsonObjectTicket.getString("headimgurl");
                            logger.info("headimgurl=" + headimgurl);
                            String privilege = jsonObjectTicket.getString("privilege");
                            logger.info("privilege=" + privilege);
                            String unionid = jsonObjectTicket.getString("unionid");
                            logger.info("unionid=" + unionid);


                            if(nickname!=null && StringUtils.isNotBlank(nickname)) {

                                WeiXinUserInfo weiXinUserInfo =new WeiXinUserInfo();
                                weiXinUserInfo.setOpenid(openid);
                                weiXinUserInfo.setNickname(nickname);
                                weiXinUserInfo.setSex(sex);
                                weiXinUserInfo.setProvince(province);
                                weiXinUserInfo.setCity(city);
                                weiXinUserInfo.setCountry(country);
                                weiXinUserInfo.setHeadimgurl(headimgurl);
                                weiXinUserInfo.setPrivilege(privilege);
                                weiXinUserInfo.setUnionid(unionid);
                                appidToUserInfoMap.put(appid, weiXinUserInfo);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return resultUserInfo;
    }
}
