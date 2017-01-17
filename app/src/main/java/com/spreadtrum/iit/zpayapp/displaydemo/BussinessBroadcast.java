package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.message.AppInformation;

/**
 * Created by SPREADTRUM\ting.long on 17-1-16.
 */

public class BussinessBroadcast {
    public static final String ACTION_BUSSINESS_EXECUTED_SUCCESS="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS";
    public static final String ACTION_BUSSINESS_EXECUTED_FAILED="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED";
    public static final String ACTION_BUSSINESS_NOT_EXECUTED="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED";
    private AppDisplayDatabaseHelper dbHelper=null;
    /**
     * 应用下载/删除完成后，发送广播
     * @param action
     * @param appInformation
     * @param bussiness "download” or "delete"
     */
    public void broadcastUpdate(String action, AppInformation appInformation, String bussiness){
        Intent intent = new Intent();
        intent.setAction(action);
        Bundle bundle = new Bundle();
        bundle.putSerializable("BUSSINESS_UPDATE",appInformation);
        bundle.putString("BUSSINESS_TYPE",bussiness);
        intent.putExtras(bundle);
        MyApplication.getContextObject().sendBroadcast(intent);
        //修改全局变量appInstalling
        MyApplication.appInstalling.put(appInformation.getIndex(),false);
        //更新数据库
        dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
        if(action.equals(ACTION_BUSSINESS_EXECUTED_SUCCESS)){
            ContentValues contentValues = new ContentValues();
            if(bussiness.equals("download")){
                contentValues.put("appinstalled", "yes");
            }
            else
                contentValues.put("appinstalled","no");
            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
            dbWrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"appname=?",new String[]{appInformation.getAppname()});
        }
//        dbHelper.getWritableDatabase();
//                SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("appinstalled", "yes");
//                dbWrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO, contentValues, "appname=?", new String[]{appInformation.getAppname()});
    }
}
