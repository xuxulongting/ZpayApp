package com.spreadtrum.iit.zpayapp.Log;

import android.os.ParcelUuid;
import android.util.Log;

/**
 * Created by SPREADTRUM\ting.long on 16-8-5.
 */
public class LogUtil {

    public static final int VERBOSE_LEVEL = 0;
    public static final int DEBUG_LEVEL = 1;
    public static final int INFO_LEVEL = 2;
    public static final int WARN_LEVEL = 3;
    public static final int ERROR_LEVEL = 4;
    public static final String TAG = "BLE";

    public static int log_level = VERBOSE_LEVEL;
    public static void verbose(String tag,String msg){
        if(log_level<=VERBOSE_LEVEL)
            Log.v(tag,msg);
    }

    public static void verbose(String msg){
        if(log_level<=VERBOSE_LEVEL)
            Log.v(TAG,msg);
    }

    public static void debug(String tag,String msg){
        if(log_level <= DEBUG_LEVEL){
            Log.d(tag,msg);
        }
    }

    public static void debug(String msg){
        if(log_level <= DEBUG_LEVEL){
            Log.d(TAG,msg);
        }
    }

    public static void info(String tag,String msg){
        if(log_level <= INFO_LEVEL){
            Log.i(tag,msg);
        }
    }

    public static void info(String msg){
        if(log_level <= INFO_LEVEL){
            Log.i(TAG,msg);
        }
    }

    public static void warn(String tag,String msg){
        if(log_level <= WARN_LEVEL){
            Log.w(tag,msg);
        }
    }

    public static void warn(String msg){
        if(log_level <= WARN_LEVEL){
            Log.w(TAG,msg);
        }
    }

    public static void error(String tag,String msg){
        if(log_level <= ERROR_LEVEL){
            Log.e(tag,msg);
        }
    }

    public static void error(String msg){
        if(log_level <= ERROR_LEVEL){
            Log.e(TAG,msg);
        }
    }

}
