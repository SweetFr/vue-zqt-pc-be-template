package me.zhengjie.modules.system.rest;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.utils.HttpClientUtil;
import me.zhengjie.vo.ZqtongResult;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：cesar.X.
 * @date ：Created in 2:38 PM 2019/5/31
 * @description：新的pc端免登方案实现，具体见https://doc.eazytec-cloud.com/tng-middleware/pc-issues.html 下面的是政府端的例子，企业端也是类似的
 * @dodified By：
 */
@RestController
@RequestMapping(value = "/newFreeLogin/")
@Validated
public class NewFreeLoginController {

    @Value("${clientId}")
    public String clientId;

    @Value("${clientSecret}")
    public String clientSecret;


    private static final String GOV = "gov";
    private static final String COM = "com";

    /**
     * 根据加密后的code，以及token解密用户的id，这边我们同时返回userid及token
     *
     * @param code 加密后台的用户id
     * @return 返回的数据#号隔开，代表的含义依次是 政府端userid，baseid（等同于labelId），token
     */
    @RequestMapping(value = "/getDecUserinfo/{type}", method = RequestMethod.GET)
    public ZqtongResult getDecUserinfo(@NotBlank(message = "参数code不能为空") String code,
                                       @NotBlank(message = "type")
                                       @PathVariable("type") String type, HttpServletResponse response) throws IOException {
        String token = getToken(type);
        if (StringUtils.isBlank(token)) {
            response.sendError(401);
            return null;
            //return ZqtongResult.build(400, "fail", "获取token失败");
        }

        // 获取解密后的用户信息
        String data = getDecUser(type, token, code);
        if (StringUtils.isBlank(token) || !data.contains("#")) {
            response.sendError(401);
            return null;
            //return ZqtongResult.build(500, "fail", "解密用户信息失败");
        }

        return ZqtongResult.build(200, "success", data + "#" + token);
    }

    private String getToken(String type) {
        String accessToken = "";
        try {
            ZqtongResult tokenResult = new ZqtongResult();
            if (type.equals(GOV)) {
                tokenResult = HttpClientUtil.doGet("https://gov-authorize.eazytec-cloud.com/v2/" +
                        "oauth/token/api?clientId=" + clientId + "&clientSecret=" + clientSecret, null);
            } else {
                tokenResult = HttpClientUtil.doGet("https://authorize.eazytec-cloud.com/v2/" +
                        "oauth/token/api?clientId=" + clientId + "&clientSecret=" + clientSecret, null);
            }

            Object dataObj = JSON.parseObject((String) tokenResult.getData());
            Object dataMap = ((Map) dataObj).get("data");
            accessToken = (String) ((Map) dataMap).get("access_token");
        } catch (Exception e) {
            accessToken = "";
        }
        return accessToken;
    }

    private String getDecUser(String type, String token, String code) {
        String decData = "";
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + token);
        try {
            ZqtongResult tokenResult = new ZqtongResult();
            if (type.equals(GOV)) {
                tokenResult = HttpClientUtil.doGet("https://gov-authorize.eazytec-cloud.com/active" +
                        "/user/code/dec?code=" + code, headers);
            } else {
                tokenResult = HttpClientUtil.doGet("https://authorize.eazytec-cloud.com/active" +
                        "/user/code/dec?code=" + code, headers);
            }


            Object dataObj = JSON.parseObject((String) tokenResult.getData());
            Object dataMap = ((Map) dataObj).get("data");
            decData = (String) dataMap;
        } catch (Exception e) {
            decData = "";
        }
        return decData;
    }
}
