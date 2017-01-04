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
        apduInfoList = new ArrayList<APDUInfo>();
    }

    public String getFinishFlag() {
        return finishFlag;
    }

    public void setFinishFlag(String finishFlag) {
        this.finishFlag = finishFlag;
    }

    public String getResultResponse() {
        return resultResponse;
    }

    public void setResultResponse(String resultResponse) {
        this.resultResponse = resultResponse;
    }

    public String getDesResponse() {
        return desResponse;
    }

    public void setDesResponse(String desResponse) {
        this.desResponse = desResponse;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String operateType;
    public List<APDUInfo> apduInfoList;
    public String taskId;
    public String sessionId;
    public String finishFlag;
    public String resultResponse;
    public String desResponse;
}
