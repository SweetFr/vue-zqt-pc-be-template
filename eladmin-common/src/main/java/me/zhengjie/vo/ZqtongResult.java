package me.zhengjie.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;

/**
 * @author Cesar
 * @date 2018/5/24.
 */
public class ZqtongResult implements Serializable {

    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    public static ZqtongResult build(Integer status, String msg, Object data) {
        return new ZqtongResult(status, msg, data);
    }


    public ZqtongResult() {

    }

    public static ZqtongResult build(Integer status, String msg) {
        return new ZqtongResult(status, msg, null);
    }

    public ZqtongResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static ZqtongResult success(Object data) {
        return new ZqtongResult(200, "success", data);
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
