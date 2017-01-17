package com.spreadtrum.iit.zpayapp.network.webservice;

import android.util.Base64;

import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.utils.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.bussiness.ResultCallback;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;

import java.util.List;
import java.util.Map;

import static java.lang.Thread.currentThread;

/**
 * Created by SPREADTRUM\ting.long on 16-11-3.
 * 获取应用列表和task id
 */

public class WebserviceHelper {
    /**
     * 获取SEID
     * @param bluetoothControl 蓝牙句柄
     * @param callback  返回SEID回调结果
     */
    public static void getSeId(final BluetoothControl bluetoothControl, final ResultCallback callback){
        //获取SeId 00A4040007A0000001510000
        byte[] command1 = {0x00,(byte)0xA4,0x04,0x00,0x07,(byte)0xA0,0x00,0x00,0x01,0x51,0x00,0x00};

        bluetoothControl.communicateWithJDSe(command1,command1.length);
        bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
            @Override
            public void callbackTSM(byte[] responseData, int responseLen) {
//                //RxJava转换线程测试用
//                String message = "test";
//                if(responseLen!=0)
//                    message = ByteUtil.bytesToHexString(responseData,responseLen);
//                Observable.just(message)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action1<String>() {
//                            @Override
//                            public void call(String s) {
//                                Toast.makeText(MyApplication.getContextObject(),"result:"+s,Toast.LENGTH_LONG).show();
//                            }
//                        });
                byte[] command2 = {(byte)0x80,(byte)0xCA,0x00,0x45,0x00};
                bluetoothControl.communicateWithJDSe(command2,command2.length);
                bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                    @Override
                    public void callbackTSM(byte[] responseData, int responseLen) {

//                        MyApplication.seId = new String(responseData,0,responseData.length-2);
                        //69168380826800042200000300000001255255
                        //45105350524400041600000300000001FFFF
                        MyApplication.seId = ByteUtil.bytesToHexString(responseData,responseLen-2);
                        callback.onSuccess(MyApplication.seId);
                        LogUtil.debug("SeId:"+MyApplication.seId);
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

    /**
     * 获取applet列表信息
     * @param bleDevAddr 蓝牙地址
     * @param callback  获取列表信息结果回调
     */
    public static void getListDataWithoutSeid(String bleDevAddr,final ResultCallback callback){
        //与蓝牙建立连接
        final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bleDevAddr);
        if (bluetoothControl!=null){
            bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                @Override
                public void onBLEPrepared() {
                    //获取SeId 00A4040007A0000001510000
                    byte[] command1 = {0x00,(byte)0xA4,0x04,0x00,0x07,(byte)0xA0,0x00,0x00,0x01,0x51,0x00,0x00};
                    bluetoothControl.communicateWithJDSe(command1,command1.length);
                    bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                        @Override
                        public void callbackTSM(byte[] responseData, int responseLen) {
//                //RxJava转换线程测试用
//                String message = "test";
//                if(responseLen!=0)
//                    message = ByteUtil.bytesToHexString(responseData,responseLen);
//                Observable.just(message)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action1<String>() {
//                            @Override
//                            public void call(String s) {
//                                Toast.makeText(MyApplication.getContextObject(),"result:"+s,Toast.LENGTH_LONG).show();
//                            }
//                        });
                            byte[] command2 = {(byte)0x80,(byte)0xCA,0x00,0x45,0x00};
                            bluetoothControl.communicateWithJDSe(command2,command2.length);
                            bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                                @Override
                                public void callbackTSM(byte[] responseData, int responseLen) {
                                    //69168380826800042200000300000001255255
                                    //45105350524400041600000300000001FFFF
                                    //断开蓝牙连接
                                    bluetoothControl.disconnectBluetooth();
                                    MyApplication.seId = ByteUtil.bytesToHexString(responseData,responseLen-2);
                                    getListDataWithSeid(MyApplication.seId,callback);
                                    LogUtil.debug("SeId:"+MyApplication.seId);
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

                @Override
                public void onBLEPrepareFailed() {

                }


            });
        }

    }

    /**
     * 获取展示的应用信息
     * @param seId
     * @param callback
     */
    public static void getListDataWithSeid(String seId, final ResultCallback callback){
        //请求类型
        final String requestType = "dbquery";
        //请求数据
        final String requestData = "applistquery";
        LogUtil.debug("THREAD","getListDataWithSeid: "+String.valueOf(currentThread().getId()));
        //创建request xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,requestData);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        LogUtil.debug(requestXmlBase64);
        //发送soap请求，并获取xml结果,从网络获取数据，使用消息的方式，因为网络获取数据是异步的
        TSMPersonalizationWebservice.getTSMInformation(requestXmlBase64, new TSMInformationCallback() {
            @Override
            public void getAppInfo(String xml) {
                LogUtil.debug("THREAD","getAppInfo: "+String.valueOf(currentThread().getId()));
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
     * 获取业务（下载，删除，同步）的任务id
     * @param seId  SE的索引信息
     * @param item   执行任务的相关applet信息
     * @param taskType    请求的任务类型
     * @param callback  网络请求结果回调
     */
    public static void getTSMTaskid(String seId, AppInformation item,String taskType,
                                    TSMInformationCallback callback){
        //请求类型
        String requestType = "dbinsert";
        //请求数据
        RequestTaskidEntity entity=MessageBuilder.getRequestTaskidEntity(item, taskType);
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,entity);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        TSMPersonalizationWebservice.getTSMInformation(requestXmlBase64,callback);
    }
}
