package com.spreadtrum.iit.zpayapp.message;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class RequestTaskidEntity {
    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getTaskcommand() {
        return taskcommand;
    }

    public void setTaskcommand(String taskcommand) {
        this.taskcommand = taskcommand;
    }

    String tasktype;
    String taskcommand;
}
