package me.zhengjie.modules.system.rest;

import me.zhengjie.vo.ZqtongResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @Description: OpenShift HealthCheck
 * @Author : maodw
 * @Date : 2018/12/01
 */
@Controller
@RequestMapping(value = "/healthcheck")
public class HealthCheckController {
    /**
     * @Description: OpenShift HealthCheck
     *
     * @param:
     *
     * @return:
     *
     * @Author: maodw
     * @Date: 2018/12/01
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ZqtongResult index() {
        return ZqtongResult.build(200, "success", "healthcheck ok");
    }
}

