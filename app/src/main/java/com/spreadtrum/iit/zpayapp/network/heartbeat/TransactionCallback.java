package com.spreadtrum.iit.zpayapp.network.heartbeat;

import com.spreadtrum.iit.zpayapp.message.APDUInfo;

/**
 * Created by SPREADTRUM\ting.long on 16-11-4.
 * 心跳线程中APDUList执行结果回调
 */
public interface TransactionCallback {
    /**
     * APDUList执行成功
     * @param apduInfo  最后一条指令的执行结果
     */
    void onTransactionSuccess(APDUInfo apduInfo);

    /**
     *心跳线程执行相关任务失败
     * @param apduInfo  最后一条执行的执行结果
     */
    void onTransactionFailed(APDUInfo apduInfo);
}
