package com.spreadtrum.iit.zpayapp.network.webservice;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.DatabaseHandler;
import com.spreadtrum.iit.zpayapp.displaydemo.AppStoreCommonAdapter;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.NetworkUtils;
import com.spreadtrum.iit.zpayapp.network.ResultCallback;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;
import com.spreadtrum.iit.zpayapp.register_login.DigtalpwdLoginActivity;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by SPREADTRUM\ting.long on 16-11-3.
 */

public class WebserviceHelper {
    /**
     * 获取seid
     * @param bluetoothControl
     */
    private static void getListDataWithoutSeid(final BluetoothControl bluetoothControl, final ResultCallback callback){
        //获取SeId 00A4040007A0000001510000
        byte[] command1 = {0x00,(byte)0xA4,0x04,0x00,0x07,(byte)0xA0,0x00,0x00,0x01,0x51,0x00,0x00};

        bluetoothControl.communicateWithJDSe(command1,command1.length);
        bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
            @Override
            public void callbackTSM(byte[] responseData, int responseLen) {
                //RxJava转换线程测试用
                String message = "test";
                if(responseLen!=0)
                    message = ByteUtil.bytesToHexString(responseData,responseLen);
                Observable.just(message)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Toast.makeText(MyApplication.getContextObject(),"result:"+s,Toast.LENGTH_LONG).show();
                            }
                        });
                byte[] command2 = {(byte)0x80,(byte)0xCA,0x00,0x45,0x00};
                bluetoothControl.communicateWithJDSe(command2,command2.length);
                bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                    @Override
                    public void callbackTSM(byte[] responseData, int responseLen) {

                        MyApplication.seId = new String(responseData,0,responseData.length-2);
                        getListDataWithSeid(MyApplication.seId,callback);
                    }

                    @Override
                    public void errorCallback() {

                    }
                });
            }

            @Override
            public void errorCallback() {

            }
        });
    }

    private static void getListDataWithSeid(String seId, final ResultCallback callback){
        //从网络获取appInformation
        final String requestType = "dbquery";
        final String requestData = "applistquery";

        //(2)从网络获取数据，使用消息的方式，因为网络获取数据是异步的
        TSMPersonalizationWebservice.getAppinfoFromWebservice(seId, requestType, requestData,
                new TSMAppInformationCallback() {
                    @Override
                    public void getAppInfo(String xml) {
                        if(xml.isEmpty()) {
                            callback.onFailed("网络异常");
                            return;
                        }
                        if(xml.equals("808")){
                            callback.onFailed("808");
                            return;
                        }
                        List<AppInformation> appList;
                        //解析xml
                        TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                        //获取List<AppInformation>
                        LogUtil.debug("get applist");
                        appList = entity.getAppInformationList();
                        //appInfoPrepared=true;
                        //获取全局变量map中的值给appList
                        for (Map.Entry<String, Boolean> entry : MyApplication.appInstalling.entrySet()) {
                            String index = entry.getKey();
                            Boolean installing = entry.getValue();
                            for (int i = 0; i < appList.size(); i++) {
                                AppInformation appInfo = appList.get(i);
                                if (appInfo.getIndex().equals(index)) {
                                    appInfo.setAppinstalling(installing);
                                }
                            }
                        }
                        callback.onSuccess(appList);
                    }
                });
    }

    /**
     * 根据seid获取到了应用列表
     * @param seId
     */
    public static void getListDataFromWebService(BluetoothControl bluetoothControl,String seId,ResultCallback callback){
        if(seId.isEmpty())
            getListDataWithoutSeid(bluetoothControl,callback);
        else
            getListDataWithSeid(seId,callback);
    }
}
