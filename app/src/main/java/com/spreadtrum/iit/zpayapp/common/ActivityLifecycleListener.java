package com.spreadtrum.iit.zpayapp.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;

/**
 * 判断app是否运行在后台,每启动或关闭一个activity，都会执行一次onActivityCreated和onActivityStopped，
 * 所以当refCount为0时，app里的所有activity都关闭了，则app运行在后台
 * 只能判断当前应用
 * Created by SPREADTRUM\ting.long on 16-10-31.
 */

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    private int refCount = 0;
    public static boolean isBackGround = false;
    public int getRefCount() {
        return refCount;
    }

    public void setRefCount(int refCount) {
        this.refCount = refCount;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtil.debug("application started");
        refCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtil.debug("application stopped");
        refCount--;
        if(refCount==0){
            isBackGround = true;
            MyApplication.dataFromNet = false;
            LogUtil.debug("application is running in background");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtil.debug("application destroyed");
    }


}
