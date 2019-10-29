package me.zhengjie.modules.system.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.zhengjie.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description :
 * @Author : jiangc
 * @Date : 2019/4/24
 */
@Controller
@RequestMapping(value = "/oauthLogin/")
public class AuthController {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    @Value("${clientId}")
    public String clientId;

    @Value("${clientSecret}")
    public String clientSecret;

    @Value("${scope}")
    public String clientScope = "read get_user_info";

    private static final String ComClientId = "platform-client";
    private static final String ComClientSecrt = "uCleGhNqxXd4Kg3n21xEPRsaBOKTPqzQ";


    private static String redirectUri = "https://api-pc-v23.app.zqtong.com/oauthLogin/auth";
    private static String govRedirectUri = "https://api-pc-v23.app.zqtong.com/oauthLogin/govAuth";

    /**
     * 静态map存放临时数据。
     */
    private static Map<String, String> data = new HashMap<>();
    private static Map<String, String> govData = new HashMap<>();

    @RequestMapping(value = "authCode")
    public String authCode(String redirect, HttpServletRequest request) {
        String targetUrl = request.getParameter("return");

        data.put("targetUrl", targetUrl);
        data.put("redirectUrl", redirect);

        Map<String, String> params = new HashMap<>();

        params.put("response_type", "code");
        params.put("client_id", ComClientId);
        params.put("redirect_uri", redirectUri);
        params.put("scope", "read get_user_info get_label_relation get_address get_application");

        return "redirect:https://authorize.eazytec-cloud.com/oauth/authorize?" + HttpUtil.buildParams(params);
    }


    @RequestMapping(value = "auth")
    public String auth(String code) {

        String targetUrl = data.get("targetUrl");
        String redirectUrl = data.get("redirectUrl");

        Map<String, String> params = new HashMap<>();

        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", ComClientId);
        params.put("client_secret", ComClientSecrt);
        params.put("redirect_uri", redirectUri);

        String result = HttpUtil.post("https://authorize.eazytec-cloud.com/oauth/token", params);
        JSONObject obj = JSON.parseObject(result);
        String token = obj.getString("access_token");

        return "redirect:" + targetUrl + "#" + redirectUrl + "?token=" + token;
    }


    @RequestMapping(value = "govAuthCode")
    public String govAuthCode(String redirect, HttpServletRequest request) {
        String targetUrl = request.getParameter("return");

        govData.put("targetUrl", targetUrl);
        govData.put("redirectUrl", redirect);

        Map<String, String> params = new HashMap<>();

        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", govRedirectUri);
        params.put("scope", clientScope);

        return "redirect:https://gov-authorize.eazytec-cloud.com/oauth/authorize?" + HttpUtil.buildParams(params);
    }


    @RequestMapping(value = "govAuth")
    public String govAuth(String code) {

        String targetUrl = govData.get("targetUrl");
        String redirectUrl = govData.get("redirectUrl");

        Map<String, String> params = new HashMap<>();

        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", govRedirectUri);

        String result = HttpUtil.post("https://gov-authorize.eazytec-cloud.com/oauth/token", params);
        JSONObject obj = JSON.parseObject(result);
        String token = obj.getString("access_token");

        return "redirect:" + targetUrl + "#" + redirectUrl + "?token=" + token;
    }

}
