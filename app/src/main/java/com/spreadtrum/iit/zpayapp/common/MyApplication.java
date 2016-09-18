package com.spreadtrum.iit.zpayapp.common;

import android.app.Application;
import android.content.Context;

import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 */
public class MyApplication extends Application {
    private static Context context;
    private String bluetoothDevAddr="";
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContextObject(){
        return context;
    }
    public void setBluetoothDevAddr(String devAddr){
        bluetoothDevAddr = devAddr;
    }
    public String getBluetoothDevAddr(){
        return bluetoothDevAddr;
    }
//    public static BluetoothControl bluetoothControl=null;
}
