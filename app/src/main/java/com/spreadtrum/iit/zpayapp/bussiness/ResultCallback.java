package com.spreadtrum.iit.zpayapp.bussiness;

/**
 * Created by SPREADTRUM\ting.long on 16-11-3.
 */

/**
 * 该接口主要用于用户登录/注册/退出/获取应用列表数据（ListData）/获取SEID的回调结果
 * @param <T>
 */
public interface ResultCallback<T> {
    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置

    /**
     *任务执行之前的操作
     */
    void onPreStart();

    /**
     *任务执行成功回调结果
     * @param response
     */
    void onSuccess(T response);

    /**
     *任务执行失败回调结果
     * @param error
     */
    void onFailed(String error);
}
