package com.spreadtrum.iit.zpayapp.common;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by SPREADTRUM\ting.long on 16-11-2.
 */

public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager activityManager;
    private ActivityManager(){

    }
    public static ActivityManager getInstance(){
        if(activityManager==null)
            activityManager = new ActivityManager();
        return activityManager;
    }

    public boolean hasActivity(){
        if (activityStack.size()>0)
            return true;
        else
            return false;
    }

    public void addActivity(Activity activity){
        if(activityStack==null){
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    public void finishAllActivity(){
        for (int i=0,size=activityStack.size();i<size;i++){
            if(activityStack.get(i)!=null){
                Activity activity = activityStack.get(i);
                if(!activity.isFinishing())
                    activity.finish();
            }
        }
        activityStack.clear();
    }

    public void finishActivity(Activity activity){
        if(activity!=null){
            activity.finish();
            activityStack.remove(activity);
            activity=null;
        }
    }
}
