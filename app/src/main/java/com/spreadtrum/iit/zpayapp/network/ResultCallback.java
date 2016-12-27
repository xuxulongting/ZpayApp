package com.spreadtrum.iit.zpayapp.network;

/**
 * Created by SPREADTRUM\ting.long on 16-11-3.
 */

/**
 * 该接口主要用于用户登录/注册/退出/获取应用列表数据（ListData）/获取SEID的回调结果
 * @param <T>
 */
public interface ResultCallback<T> {
    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    void onPreStart();
    void onSuccess(T response);
    void onFailed(String error);
//    //该方法在非UI线程
//    void onApduExcutedSuccess(T response);
//    //该方法在非UI线程
//    void onApduExcutedFailed(String error);
//    //该方法在非UI线程
//    void onApduEmpty();
//    //
//    void onNetworkError(String errMsg);
}
