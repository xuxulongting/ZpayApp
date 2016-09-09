package com.spreadtrum.iit.zpayapp.network.tcp;

/**
 * Created by SPREADTRUM\ting.long on 16-9-8.
 */
public interface TsmTaskCompleteListener {
    void onTaskExecutedSuccess();
    void onTaskExecutedFailed();
}
