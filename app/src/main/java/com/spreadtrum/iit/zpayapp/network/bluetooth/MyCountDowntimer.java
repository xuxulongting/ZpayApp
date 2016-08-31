package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.os.CountDownTimer;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;

/**
 * Created by SPREADTRUM\ting.long on 16-8-29.
 */
public class MyCountDowntimer extends CountDownTimer {
    private CountDownTimerListener listener;
    public MyCountDowntimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onFinish() {
        LogUtil.debug(TAG,"timeup!!!");
        listener.onTimeup();
    }

    public interface CountDownTimerListener{
        void onTimeup();
    }

    public void setCountDownTimerListerner(CountDownTimerListener listerner){
        this.listener = listerner;
    }

    public static String TAG = "BLE";
}
