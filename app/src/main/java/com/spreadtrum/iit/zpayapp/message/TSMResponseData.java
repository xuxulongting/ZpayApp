package com.spreadtrum.iit.zpayapp.message;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.PUT;

/**
 * Created by SPREADTRUM\ting.long on 16-11-4.
 */

public class TSMResponseData {
    public List<APDUInfo> getApduInfoList() {
        return apduInfoList;
    }

    public void setApduInfoList(List<APDUInfo> apduInfoList) {
        this.apduInfoList = apduInfoList;
    }

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

    public TSMResponseData(){

    }

    public List<APDUInfo> apduInfoList;
    public String taskId;
    public String sessionId;
}
