package com.spreadtrum.iit.zpayapp.message;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 */
public class AppInformation {
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppsize() {
        return appsize;
    }

    public void setAppsize(String appsize) {
        this.appsize = appsize;
    }

    public String getApptype() {
        return apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    public String getSpname() {
        return spname;
    }

    public void setSpname(String spname) {
        this.spname = spname;
    }

    public String getAppdesc() {
        return appdesc;
    }

    public void setAppdesc(String appdesc) {
        this.appdesc = appdesc;
    }

    public String getAppinstalled() {
        return appinstalled;
    }

    public void setAppinstalled(String appinstalled) {
        this.appinstalled = appinstalled;
    }
    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    private String index;//索引
    private String picurl;//图片下载url
    private String appname;//应用名称
    private String appsize;//应用空间大小
    private String apptype;//应用类型
    private String spname;//应用提供商
    private String appdesc;//应用简介
    private String appinstalled;//应用是否已安装 yes/not
    private String appid;//应用标识
}
