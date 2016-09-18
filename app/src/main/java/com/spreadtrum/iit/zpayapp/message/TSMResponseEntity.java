package com.spreadtrum.iit.zpayapp.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 */
public class TSMResponseEntity {
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReqtype() {
        return reqtype;
    }

    public void setReqtype(String reqtype) {
        this.reqtype = reqtype;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public List<AppInformation> getAppInformationList() {
        return appInformationList;
    }

    public void setAppInformationList(List<AppInformation> appInformationList) {
        this.appInformationList = appInformationList;
    }

    private String version;
    private String reqtype;
    private String result;
    private List<AppInformation> appInformationList = new ArrayList<AppInformation>();//应用查询结果
    private String taskId;//获取TSM task id结果

}
