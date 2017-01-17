package com.spreadtrum.iit.zpayapp.message;

/**
 * Created by SPREADTRUM\ting.long on 16-11-4.
 * 用于心跳线程中发送的心跳请求包中的参数
 */

public class TSMRequestData {
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSeId() {
        return seId;
    }

    public void setSeId(String seId) {
        this.seId = seId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String taskId;
    public String sessionId;
    public String seId;
    public String imei;
    public String phone;
    public String type;
}
