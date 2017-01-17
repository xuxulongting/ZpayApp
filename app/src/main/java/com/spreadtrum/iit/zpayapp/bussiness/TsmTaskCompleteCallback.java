package com.spreadtrum.iit.zpayapp.bussiness;

/**
 * Created by SPREADTRUM\ting.long on 16-9-27.
 * 业务执行结果回调，包括下载/删除/同步/个人化等
 * public void transactBussiness(final AppInformation item, String taskType,
 * final TsmTaskCompleteCallback completeCallback)；
 */
public interface TsmTaskCompleteCallback {
    /**
     *业务成功执行结果回调
     */
    void onTaskExecutedSuccess();

    /**
     *业务执行失败结果回调
     */
    void onTaskExecutedFailed();

    /**
     *业务未执行结果回调（由于TCP未连接，或蓝牙未连接）
     */
    void onTaskNotExecuted();
}

