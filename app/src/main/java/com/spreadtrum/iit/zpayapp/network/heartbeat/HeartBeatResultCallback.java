package com.spreadtrum.iit.zpayapp.network.heartbeat;

/**
 * Created by SPREADTRUM\ting.long on 16-12-16.
 */

public interface HeartBeatResultCallback<T> {
    //    //该方法在非UI线程
    void onApduExcutedSuccess(T response);
    //该方法在非UI线程
    void onApduExcutedFailed(T error);
    //该方法在非UI线程
    void onApduEmpty();
    //
    void onNetworkError(T errMsg);
}
