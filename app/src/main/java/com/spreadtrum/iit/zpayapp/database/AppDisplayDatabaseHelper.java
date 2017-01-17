package com.spreadtrum.iit.zpayapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SPREADTRUM\ting.long on 16-8-24.
 */
public class AppDisplayDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String TABLE_APPINFO = "appinfo";
    public static final String CREATE_TABLE_APPINFO = "create table "+TABLE_APPINFO+" ("
            + "_id integer primary key autoincrement, "
            + "appindex text not null, "
            + "picurl text not null,"
            + "appname text not null,"
            + "appsize text,"
            + "apptype text,"
            + "spname text,"
            + "appdesc text,"
            + "appinstalled text not null,"
            + "applocked text not null,"
            + "appid text not null, "
            + "localpicpath text)";
    public AppDisplayDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_APPINFO);
        //Toast.makeText(mContext,"create table",Toast.LENGTH_LONG).show();
        //LogUtil.debug("create table appinfo");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
