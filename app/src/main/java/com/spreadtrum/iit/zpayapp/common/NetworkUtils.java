package com.spreadtrum.iit.zpayapp.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.Network;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by SPREADTRUM\ting.long on 16-11-2.
 */

public class NetworkUtils {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            return (networkInfo!=null && networkInfo.isConnected());
        }
    }

    public static boolean isWifiConnected(Context context){
        return isNetworkConnected(context,TYPE_WIFI);
    }

    public static boolean isMobileConnected(Context context){
        return isNetworkConnected(context,TYPE_MOBILE);
    }

    private static boolean isNetworkConnected(Context context,int type){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null)
            return false;
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(type);
            return (networkInfo!=null && networkInfo.isConnected());
        }
        else {
            android.net.Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (android.net.Network network:networks){
                networkInfo = connectivityManager.getNetworkInfo(network);
                if(networkInfo!=null && networkInfo.isConnected()==true && networkInfo.getType()==type)
                    return true;
            }
            return false;
        }
    }
}
