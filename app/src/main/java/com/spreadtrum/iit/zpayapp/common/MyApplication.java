package com.spreadtrum.iit.zpayapp.common;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 */
public class MyApplication extends Application {
    private static Context context;
    private String bluetoothDevAddr="";
    public static Map<String,Boolean> appInstalling = new HashMap<>();
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
    public static Handler handler = new Handler(){
        public void handleMessage(Message msg){
           switch (msg.what){
               case DOWNLOAD_SUCCESS:
                   Toast.makeText(MyApplication.getContextObject(),"绑卡成功",Toast.LENGTH_LONG).show();
                   break;
               case DOWNLOAD_FAILED:
                   Toast.makeText(MyApplication.getContextObject(),"绑卡失败",Toast.LENGTH_LONG).show();
                   break;
               case DELETE_SUCCESS:
                   Toast.makeText(MyApplication.getContextObject(),"取消绑卡成功",Toast.LENGTH_LONG).show();
                   break;
               case DELETE_FAILED:
                   Toast.makeText(MyApplication.getContextObject(),"取消绑卡失败",Toast.LENGTH_LONG).show();
                   break;
           }
        }
    };
    public static boolean dataFromNet = false;
    public static final int DOWNLOAD_SUCCESS=0;
    public static final int DOWNLOAD_FAILED=1;
    public static final int DELETE_SUCCESS=2;
    public static final int DELETE_FAILED=3;
}
