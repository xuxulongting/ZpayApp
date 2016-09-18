package com.spreadtrum.iit.zpayapp.message;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
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
    public int getIconviewid() {
        return iconviewid;
    }

    public void setIconviewid(int iconViewId) {
        this.iconviewid = iconViewId;
    }

    public boolean isAppinstalling() {
        return appinstalling;
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
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
    private String index;//索引
    private String picurl;//图片下载url
    private String appname;//应用名称
    private String appsize;//应用空间大小
    private String apptype;//应用类型
    private String spname;//应用提供商
    private String appdesc;//应用简介
    private String appinstalled="not";//应用是否已安装 yes/not
    private String appid;//应用标识
    private int iconviewid;//本地图片资源id
    private Bitmap bitmap=null;//下载的图片资源
    private Uri imageUri = null;//本地缓存的图片资源
    private boolean appinstalling=false;//应用是否正在安装 true/false
    private int indexForlistview;   //在listview中的位置，与TSM数据库无关
}
