package cn.objectspace.daemon.pojo.entity;

import java.io.Serializable;

/**
* @Description: ocdae.os配置文件序列化对象
* @Author: NoCortY
* @Date: 2020/3/6
*/
public class OcdaePO implements Serializable {

    private static final long serialVersionUID = 2530970366896004807L;
    private Integer userId;
    private String pingUrl;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPingUrl() {
        return pingUrl;
    }

    public void setPingUrl(String pingUrl) {
        this.pingUrl = pingUrl;
    }
}
