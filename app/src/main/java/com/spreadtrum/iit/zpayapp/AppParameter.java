package com.spreadtrum.iit.zpayapp;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by SPREADTRUM\ting.long on 16-7-28.
 */
public class AppParameter {

    public AppParameter(BitmapDrawable bitmapDrawable,String appType) {
        this.bitmapDrawable = bitmapDrawable;
        this.appType = appType;
    }

    public AppParameter() {

    }

    public BitmapDrawable getBitmapDrawable() {
        return bitmapDrawable;
    }

    public String getAppType() {
        return appType;
    }

    public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
        this.bitmapDrawable = bitmapDrawable;
    }

    public void setAppType(String text) {
        this.appType = text;
    }

    private BitmapDrawable bitmapDrawable = null;
    private String appType;
}
