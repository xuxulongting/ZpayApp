package com.spreadtrum.iit.zpayapp.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.spreadtrum.iit.zpayapp.message.AppInformation;

import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-22.
 */
public class DatabaseHandler {
    //插入数据库,删除原来数据，再重新添加，使用原子操作
    public void insertDB(SQLiteDatabase db, List<AppInformation> appInformationList){
        //先删除所有行，开启事务
        db.beginTransaction();
        db.delete(AppDisplayDatabaseHelper.TABLE_APPINFO,null,null);
        for(int i=0;i<appInformationList.size();i++){
            ContentValues contentValues = new ContentValues();
            AppInformation info = appInformationList.get(i);
            contentValues.put("appindex",info.getIndex());
            contentValues.put("picurl",info.getPicurl());
            contentValues.put("appname",info.getAppname());
            contentValues.put("appsize",info.getAppsize());
            contentValues.put("apptype",info.getApptype());
            contentValues.put("spname",info.getSpname());
            contentValues.put("appdesc",info.getAppdesc());
            contentValues.put("appinstalled",info.getAppinstalled());
            contentValues.put("applocked",info.getApplocked());
            contentValues.put("appid",info.getAppid());
            //如果更新失败，则插入？影响效率，直接删除，再插入
            //if(db.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"appindex=?",new String[]{info.getIndex()})==0)
            db.insert(AppDisplayDatabaseHelper.TABLE_APPINFO,null,contentValues);
            contentValues.clear();
        }
        //事务已经执行成功
        db.setTransactionSuccessful();
        //关闭事务
        db.endTransaction();
    }
}
