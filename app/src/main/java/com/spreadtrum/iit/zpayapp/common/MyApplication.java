package com.spreadtrum.iit.zpayapp.common;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.network.HeartBeatThread;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 */
public class MyApplication extends Application {
    private static Context context;
    private String bluetoothDevAddr="";
    public static Map<String,Boolean> appInstalling = new HashMap<>();
    public static MyApplication getInstance(){
        return new MyApplication();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        context = getApplicationContext();
        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
        //创建Thread，发送心跳包
        new HeartBeatThread().start();
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
                   Toast.makeText(MyApplication.getContextObject(),"解绑成功",Toast.LENGTH_LONG).show();
                   break;
               case DELETE_FAILED:
                   Toast.makeText(MyApplication.getContextObject(),"解绑失败",Toast.LENGTH_LONG).show();
                   break;
           }
        }
    };

    public PackageInfo getPackageInfo(){
        PackageInfo info = null;
        String pkgName = context.getPackageName();
        try {
            info = context.getPackageManager().getPackageInfo(pkgName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info==null){
            info = new PackageInfo();
        }
        return info;
    }

    public static boolean dataFromNet = false;
    public static final int DOWNLOAD_SUCCESS=0;
    public static final int DOWNLOAD_FAILED=1;
    public static final int DELETE_SUCCESS=2;
    public static final int DELETE_FAILED=3;

    public static String seId="";//"451000000000000020160328000000010003";

//    public static final String WEBSERVICE_PATH = "http://10.0.64.120:6893/SPRDTSMDbService.asmx";
}
