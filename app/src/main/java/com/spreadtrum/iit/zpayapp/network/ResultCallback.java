package com.spreadtrum.iit.zpayapp.network;

/**
 * Created by SPREADTRUM\ting.long on 16-11-3.
 */

public interface ResultCallback<T> {
    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    void onPreStart();
    //该方法在非UI线程
    void onSuccess(T response);
    //该方法在非UI线程
    void onFailed(String error);
}
