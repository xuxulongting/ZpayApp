package com.spreadtrum.iit.zpayapp.message;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 * TSM返回的应用列表中Applet信息
 */
public class AppInformation implements Serializable{
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

    public boolean isAppinstalling(String appindex) {
       //获取全局变量map中的值给appList
        for (Map.Entry<String, Boolean> entry : MyApplication.appInstalling.entrySet()) {
            String index = entry.getKey();
            Boolean installing = entry.getValue();
            if (appindex.equals(index)) {
               return installing;
            }
        }
        return false;
    }

    public void setAppinstalling(boolean appinstalling) {
        this.appinstalling = appinstalling;
    }
    public int getIndexForlistview() {
        return indexForlistview;
    }

    public void setIndexForlistview(int indexForlistview) {
        this.indexForlistview = indexForlistview;
    }
    public String getLocalpicpath() {
        return localpicpath;
    }

    public void setLocalpicpath(String localpicpath) {
        this.localpicpath = localpicpath;
    }

    public AppInformation(){

    }
    public AppInformation(String index, String picurl, String appname, String appsize,
                          String apptype, String spname, String appdesc, String appinstalled,
                          String appid, boolean appinstalling, int indexForlistview, String localpicpath,String applocked) {
        this.index = index;
        this.picurl = picurl;
        this.appname = appname;
        this.appsize = appsize;
        this.apptype = apptype;
        this.spname = spname;
        this.appdesc = appdesc;
        this.appinstalled = appinstalled;
        this.appid = appid;
        this.appinstalling = appinstalling;
        this.indexForlistview = indexForlistview;
        this.localpicpath = localpicpath;
        this.applocked = applocked;
    }
    public String getApplocked() {
        return applocked;
    }

    public void setApplocked(String applocked) {
        this.applocked = applocked;
    }
    public boolean isPicdownloading() {
        return picdownloading;
    }

    public void setPicdownloading(boolean picdownloading) {
        this.picdownloading = picdownloading;
    }
    private String index;//索引
    private String picurl;//图片下载url
    private String appname;//应用名称
    private String appsize;//应用空间大小
    private String apptype;//应用类型
    private String spname;//应用提供商
    private String appdesc;//应用简介
    private String appinstalled="not";//应用是否已安装 yes/not
    private String applocked="not";//应用是否已锁定 yes/not
    private String appid;//应用标识
    private boolean appinstalling=false;//应用是否正在安装 true/false
    private int indexForlistview;   //在listview中的位置，与TSM数据库无关
    private String localpicpath;
    private boolean picdownloading=false;



}
