package com.spreadtrum.iit.zpayapp.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SPREADTRUM\ting.long on 16-11-7.
 */

public class AppGlobal {
    //使用金电（JD）蓝牙手环
    public static final boolean JDBLE =true;

    private static String bluetoothDevAddr="";
    public static boolean isOperated = false;

    public static void setBluetoothDevAddr(String devAddr){
        bluetoothDevAddr = devAddr;
    }
    public static String getBluetoothDevAddr(){
        return bluetoothDevAddr;
    }
}
