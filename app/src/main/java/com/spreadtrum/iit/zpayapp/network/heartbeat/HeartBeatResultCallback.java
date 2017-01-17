package com.spreadtrum.iit.zpayapp.network.heartbeat;

/**
 * Created by SPREADTRUM\ting.long on 16-12-16.
 * 心跳线程执行结果回调（包括锁定/解锁等）
 */

public interface HeartBeatResultCallback<T> {
    /**
     *心跳线程中执行相关任务成功
     * @param response
     */
    void onApduExcutedSuccess(T response);

    /**
     *心跳线程中执行相关任务失败
     * @param error
     */
    void onApduExcutedFailed(T error);

    /**
     *心跳线程中没有相关任务
     */
    void onApduEmpty();

    /**
     *心跳线程中网络出错
     * @param errMsg
     */
    void onNetworkError(T errMsg);
}
