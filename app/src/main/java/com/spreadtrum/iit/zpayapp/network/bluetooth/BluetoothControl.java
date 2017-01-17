package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.utils.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.currentThread;


/**
 * Created by SPREADTRUM\ting.long on 16-8-10.
 */
public class BluetoothControl {


    private BluetoothService bluetoothService;
    private Context mContext;
    private String selBluetoothDevAddr;
    private List<BluetoothGattService> gattServiceList = new ArrayList<>();
    private BluetoothGattService seCommService;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private static BluetoothControl bluetoothControl=null;
    private BLEPreparedCallbackListener blePreparedCallbackListener;



    private boolean bStopTransferApdu = false;
    //展讯蓝牙
    private int sendTime=0;
    private int sendCount=0;
    //金电蓝牙手环
    int apduSentLength = 0;     //已发送的apdu指令长度（蓝牙每次发送20字节）
    int packages;               //待发送的包数

    /**
     * 展讯蓝牙相关参数
     */
    public static String SE_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static String NOTIFY_CHARACTERISTIC = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static String WRITE_CHARACTERISTIC = "0003cdd2-0000-1000-8000-00805f9b0131";
    /**
     * 金电蓝牙手环相关参数
     */
    public static String JD_SERVICE = "1470ff10-620a-3973-7c78-9cfff0876abd";
    public static String JD_WRITE_CHARACTERISTIC = "1470ff11-620a-3973-7c78-9cfff0876abd";
    public static String JD_NOTIFY_CHARACTERISTIC = "1470ff12-620a-3973-7c78-9cfff0876abd";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final byte APDU_SEND = 0x02;
    public static final byte APDU_RECV = 0x12;
    public static final byte SETPARA_SEND = 0x01;
    public static final byte SETPARA_RECV = 0x11;
    public static final byte CHECKPARA_SEND = 0x03;
    public static final byte CHECKPARA_RECV = 0x13;
    public static final byte PARAMCODE_OPENSE = 0x03;
    public static final byte PARAMCODE_CLOSESE = 0x04;

    public boolean isbStopTransferApdu() {
        return bStopTransferApdu;
    }

    public void setbStopTransferApdu(boolean bStopTransferApdu) {
        this.bStopTransferApdu = bStopTransferApdu;
    }

    /**
     * 获取BluetoothService对象
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.debug("onServiceConnected");
            bluetoothService = ((BluetoothService.LocalBinder) iBinder).getService();
            //初始化蓝牙设备，包括检查设备是否支持BLE，获取Bluetooth Adater
            if(bluetoothService.initialize()==false){
                Toast.makeText(mContext,"初始化蓝牙失败",Toast.LENGTH_SHORT).show();
                //蓝牙初始化失败，将bluetoothControl赋值为null，可以重新bindService();
                bluetoothControl=null;
                //解绑BluetoothService服务
                bluetoothUnbindService();
                return;
            }
            //注册receiver
            mContext.registerReceiver(gattUpdateReceiver,makeGattUpdateIntentFilter());
            //connect gattServer
            if (!bluetoothService.connect(selBluetoothDevAddr))
            {
                Toast.makeText(mContext,"连接Gatt Server失败",Toast.LENGTH_SHORT).show();
                bluetoothControl=null;
                //解绑BluetoothService服务
                bluetoothUnbindService();
                return;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.debug("onServiceDisconnected");
            //关闭连接 gatt can only handle sevral connections at a time (BluetoothGatt documentation)
            if(bluetoothService!=null) {
                //FIXME
                LogUtil.debug("release blueGatt resources.");
                bluetoothService.closeBluetoothGatt();
                bluetoothService = null;
            }

        }
    };

    public void disconnectBluetooth(){
        //disconnect from gattserver,release ble resources
        if (bluetoothService!=null){
            bluetoothService.disconnect();
        }
    }

    public void setSeCallbackTSMListener(SECallbackTSMListener listener){
        if(bluetoothService!=null){
            bluetoothService.setSeCallbackTSMListener(listener);
        }
    }

    /**
     * 设置回调实例
     * @param listener
     */
    public void setBlePreparedCallbackListener(BLEPreparedCallbackListener listener){
        this.blePreparedCallbackListener = listener;
    }

    /**
     * 广播接收器，蓝牙连接GATT Server，发现服务等回调结果（非UI线程）后，发送广播给主线程
     */
    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action){
                case BluetoothService.ACTION_GATT_CONNECTED:
                    // Attempts to discover services after successful connection.
                    boolean bDiscoveryService = bluetoothService.discoverServices();
                    LogUtil.info(TAG, "Attempting to start service discovery:" + bDiscoveryService);//bluetoothGatt.discoverServices());
                    break;
                case BluetoothService.ACTION_GATT_DISCONNECTED:
                    //Gatt Server连接失败，释放BluetoothControl（单例模式）对象，并unbindService，当重新发起数据请求时，重新创建BluetoothControl实例
//                    bluetoothUnbindService();
                    //重新连接Gatt Server
//                    bluetoothService.connect(selBluetoothDevAddr);
                    break;
                case BluetoothService.ACTION_GATT_SERVICES_DISCOVERED:
                    //运行在主线程中
                    LogUtil.debug(TAG,"services discovered，thread is:"+currentThread().getId());
                    gattServiceList = bluetoothService.getSupportedGattServices();
                    if(true){//AppConfig.JDBLE){
                        seCommService = getSpecialGattService(gattServiceList,JD_SERVICE);
                        notifyCharacteristic = getSpecialCharacteristic(seCommService,JD_NOTIFY_CHARACTERISTIC);
                        writeCharacteristic = getSpecialCharacteristic(seCommService,JD_WRITE_CHARACTERISTIC);
                        bluetoothService.setCharacteristicNotification(notifyCharacteristic,true);

                        //延时1000 ms,否则无法写
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //打开SE通道
                        byte[] openSeAccess = {PARAMCODE_OPENSE};
                        setBLEParameter(openSeAccess,openSeAccess.length);
                        bluetoothService.setOpenSECallbackListener(new OpenSECallbackListener() {
                            @Override
                            public void onSEOpenedSuccess() {
                                blePreparedCallbackListener.onBLEPrepared();
                            }

                            @Override
                            public void onSEOpenedFailed() {

                            }
                        });
                        //查看电量
//                        byte[] checkPower = {0x01};
//                        checkBLEParameter(checkPower,checkPower.length);
                    }
                    else
                    {
                        seCommService = getSpecialGattService(gattServiceList,SE_SERVICE);
                        notifyCharacteristic = getSpecialCharacteristic(seCommService,NOTIFY_CHARACTERISTIC);
                        writeCharacteristic = getSpecialCharacteristic(seCommService,WRITE_CHARACTERISTIC);
                        bluetoothService.setCharacteristicNotification(notifyCharacteristic,true);
                        getAuthentication(writeCharacteristic,"123456");
                        LogUtil.warn(TAG,"WRITE PWD");
                    }

                    break;
                case BluetoothService.ACTION_DATA_AVAILABLE:
                    byte []responseData = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                    break;
            }
        }
    };


    /**
     * private构造函数
     * @param context
     * @param selBleDevAddr
     */
    private BluetoothControl(Context context,String selBleDevAddr){
        this.mContext = context;
        this.selBluetoothDevAddr = selBleDevAddr;
        Intent intent = new Intent(context,BluetoothService.class);
        boolean bBind = context.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
        if(bBind==false){
            Toast.makeText(context,"bind service failed",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 单例模式，获取蓝牙通信静态实例
     * @param context
     * @param selBleDevAddr 蓝牙设备地址
     * @return
     */
    public static BluetoothControl getInstance(Context context,String selBleDevAddr){
        if(bluetoothControl==null){
            bluetoothControl = new BluetoothControl(context,selBleDevAddr);
        }else{
//            if (bluetoothControl.bluetoothService.getBluetoothGattConnectionState()==
//                    bluetoothControl.bluetoothService.STATE_DISCONNECTING)
//                return null;
//            //如果BluetoothControl实例已经创建，则延时，先返回bluetoothControl对象，再回调onBLEPrepared()
//            createNewThread();
            if (bluetoothControl!=null && bluetoothControl.bluetoothService!=null) {
                if (bluetoothControl.bluetoothService.getBluetoothGattConnectionState()
                        == bluetoothControl.bluetoothService.STATE_DISCONNECTED) {
                    LogUtil.debug("getBluetoothGattConnectionState:STATE_DISCONNECTED");
                    MyApplication app = (MyApplication) MyApplication.getContextObject();
                    if (!bluetoothControl.bluetoothService.connect(app.getBluetoothDevAddr()))
                        return null;
                } else if (bluetoothControl.bluetoothService.getBluetoothGattConnectionState()
                        == bluetoothControl.bluetoothService.STATE_CONNECTING ||
                        bluetoothControl.bluetoothService.getBluetoothGattConnectionState() ==
                                bluetoothControl.bluetoothService.STATE_DISCONNECTING) {
                    LogUtil.debug("getBluetoothGattConnectionState:STATE_CONNECTING OR STATE_DISCONNECTING");
                    if (!bluetoothControl.bluetoothService.isBlluetoothEnabled()){
                        bluetoothControl.bluetoothService.setBluetoothGattConnectionState(
                                bluetoothControl.bluetoothService.STATE_DISCONNECTED);
                    }
                    return null;
                }
                else {
                    LogUtil.debug("getBluetoothGattConnectionState:STATE_CONNECTED");
//                    return bluetoothControl;
//                    new BussinessTransaction().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED,new AppInformation(),"notexecuted");
                    if (!bluetoothControl.bluetoothService.isBlluetoothEnabled()){
                        bluetoothControl.bluetoothService.setBluetoothGattConnectionState(
                                bluetoothControl.bluetoothService.STATE_DISCONNECTED);
                    }
                    return null;
                }
            }

        }
        return bluetoothControl;
    }

    /**
     * 创建新的线程，在这个新的线程里，与TSM和SE交互
     */
    public static void createNewThread(){
        LogUtil.debug("DelayThread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (bluetoothControl!=null && bluetoothControl.bluetoothService!=null) {
                        if (bluetoothControl.bluetoothService.getBluetoothGattConnectionState()
                                == bluetoothControl.bluetoothService.STATE_CONNECTED) {
                            LogUtil.debug("getBluetoothGattConnectionState:STATE_CONNECTED");
                            bluetoothControl.blePreparedCallbackListener.onBLEPrepared();
                        } else if (bluetoothControl.bluetoothService.getBluetoothGattConnectionState()
                                == bluetoothControl.bluetoothService.STATE_DISCONNECTED) {
                            MyApplication app = (MyApplication) MyApplication.getContextObject();
                            bluetoothControl.bluetoothService.connect(app.getBluetoothDevAddr());
                            LogUtil.debug("getBluetoothGattConnectionState:STATE_DISCONNECTED");
                        }
                    }
                } catch (InterruptedException e) {
                    LogUtil.debug(e.getMessage());
//                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void bluetoothUnregisterReceiver(){

        mContext.unregisterReceiver(gattUpdateReceiver);
    }

    /**
     * 与蓝牙服务解绑,停止蓝牙服务
     */
    public void bluetoothUnbindService()
    {
        LogUtil.debug("ubindService");
        if (bluetoothService!=null)
            bluetoothService.closeBluetoothGatt();
        mContext.unbindService(serviceConnection);
//        bluetoothService = null;
        //当再次获取BluetoothControl实例时，重新bindService()
        bluetoothControl = null;
    }

    /**
     * 获取蓝牙设备的某个服务
     * @param listGattService
     * @param strUuid
     * @return
     */
    public BluetoothGattService getSpecialGattService(List<BluetoothGattService> listGattService,String strUuid){
        for(BluetoothGattService gattService : listGattService){
            if(gattService.getUuid().toString().equals(strUuid))
                return gattService;
        }
        return null;
    }

    /**
     * 获取蓝牙设备某个特性
     * @param gattService
     * @param strUuid
     * @return
     */
    public BluetoothGattCharacteristic getSpecialCharacteristic(BluetoothGattService gattService,String strUuid){
        List<BluetoothGattCharacteristic> gattCharacteristicList = gattService.getCharacteristics();
        for(BluetoothGattCharacteristic gattCharacteristic :gattCharacteristicList){
            if(gattCharacteristic.getUuid().toString().equals(strUuid))
                return gattCharacteristic;
        }
        return null;
    }

    /**
     * 展讯蓝牙，输入PIN
     * @param gattCharacteristic
     * @param pwd
     */
    public void getAuthentication(BluetoothGattCharacteristic gattCharacteristic,String pwd){
        gattCharacteristic.setValue(pwd.getBytes());
        bluetoothService.wirteCharacteristic(gattCharacteristic);
    }


    /**
     * 设置蓝牙参数：1 打开SE交互访问通道；2 关闭SE交互访问通道
     * @param byteofdata
     * @param length
     */
    public void setBLEParameter(byte []byteofdata,int length){
        byte[] data = new byte[4+length];
        data[0]=0x10;
        data[1]=SETPARA_SEND;
        data[2]=(byte)(length>>8);
        data[3]=(byte)(length & 0xFF);
        System.arraycopy(byteofdata,0,data,4,length);
        writeCharacteristic.setValue(data);
        bluetoothService.wirteCharacteristic(writeCharacteristic);
    }

    /**
     * 查看蓝牙手环电量
     * @param byteofdata
     * @param length
     */
    public void  checkBLEParameter(byte[] byteofdata,int length){
        byte[] data = new byte[4+length];
        data[0]=0x10;
        data[1]=CHECKPARA_SEND;
        data[2]=(byte)(length>>8);
        data[3]=(byte)(length & 0xFF);
        System.arraycopy(byteofdata,0,data,4,length);;
        writeCharacteristic.setValue(data);
        bluetoothService.wirteCharacteristic(writeCharacteristic);
    }

    /**
     * 通过蓝牙与SE交互，发送数据，并获取响应
     * @param byteofdata    发送给SE的数据
     * @param length        发送数据的长度
     * @param callback      获取SE响应的回调方法
     */
    public void communicateWithSe(final byte[] byteofdata, final int length, SECallbackTSMListener callback){
        if(callback==null)
            return;
        //设置回调，当BluetoothService中蓝牙收到数据以后，就会调用callbackTSM()
        setSeCallbackTSMListener(callback);
        int packageNum=0;           //包序号
        int total_package;      //总包数
        //计算total_package
        if(length<=14)
            total_package = 1;
        else {
            int mod = (length - 14) % 19; //蓝牙最后一包是否为20byte
            total_package =(length - 14) / 19;
            if(mod>0){
                total_package+=2;
            }
            else
                total_package+=1;
        }
        packages = total_package;
        byte[] header = new byte[6];
        header[0] = (byte)(((byte)(total_package << 4)) | ((byte)(packageNum))) ;
        header[1] = APDU_SEND;
        header[2] = (byte)((length + 2)>>8);
        header[3] = (byte)((length+2) & 0xFF);
        header[4] = (byte)(length >> 8);
        header[5] = (byte)(length & 0xFF);
        //当APDU指令包<14字节，则总包数为1包
        if(length<=14) {
            byte[] data = new byte[6+length];
            System.arraycopy(header,0,data,0,6);
            System.arraycopy(byteofdata,0,data,6,length);
            writeCharacteristic.setValue(data);
            bluetoothService.wirteCharacteristic(writeCharacteristic);
            LogUtil.debug(TAG,"send to BLE:"+ByteUtil.bytesToHexString(data,data.length));
            //apduLength = 0;
            //apduSentLength = length;
        }
        else {
            //当APDU指令包>14字节，总包数大于1包
            final byte[] data = new byte[20];
            final int mod = (length - 14) % 19; //蓝牙最后一包是否为20byte
            System.arraycopy(header, 0, data, 0, 6);
            System.arraycopy(byteofdata, 0, data, 6, 14);
            writeCharacteristic.setValue(data);
            bluetoothService.wirteCharacteristic(writeCharacteristic);
            bluetoothService.bWriteCharacteristic = false;
            LogUtil.debug(TAG, "send to BLE:" + ByteUtil.bytesToHexString(data, data.length));
            //apduLength -= 14;
            apduSentLength = 14;
            packages -= 1;
            bluetoothService.setBleCallbackListener(new BluetoothService.BLECallbackListener() {
                @Override
                public void onResponseWrite(int para) {
                    byte firstByte = (byte) para;
                    int totalPackage = (firstByte & 0xF0) >> 4;
                    int packageNo = (firstByte & 0x0F);
                    if ((packages > 1) || ((packages == 1) && (mod == 0))) {
                        //packageNum+=1;
                        data[0] = (byte) (((byte) (totalPackage << 4)) | ((byte) (packageNo + 1)));
                        System.arraycopy(byteofdata, apduSentLength, data, 1, 19);
//                        //确保上次写完成
//                        while (!bluetoothService.bWriteCharacteristic) {
//
//                        }
                        writeCharacteristic.setValue(data);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
//                        bluetoothService.bWriteCharacteristic=false;
                        LogUtil.debug(TAG, "send to BLE:" + ByteUtil.bytesToHexString(data, data.length));
                        //apduLength -= 19;
                        apduSentLength += 19;
                        packages -= 1;
                    } else if ((packages == 1) && (mod != 0)) {
                        byte[] lastPackage = new byte[mod + 1];
                        //packageNum+=1;
                        lastPackage[0] = (byte) (((byte) (totalPackage << 4)) | ((byte) (packageNo + 1)));
                        System.arraycopy(byteofdata, apduSentLength, lastPackage, 1, mod);
                        //确保上次写完成
//                        while (!bluetoothService.bWriteCharacteristic) {
//
//                        }
                        writeCharacteristic.setValue(lastPackage);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
//                        bluetoothService.bWriteCharacteristic=false;
                        LogUtil.debug(TAG, "send to BLE:" + ByteUtil.bytesToHexString(lastPackage, lastPackage.length));
//                        apduLength-=mod;
                        apduSentLength += mod;
                        packages -= 1;
                        if (apduSentLength == length) {
                            //apduSentLength=0;
                            LogUtil.debug("apdu send success!");
                        } else
                            LogUtil.debug("apdu send failed!");
                    }

                }

                @Override
                public void onResponseRead(int receiveTime) {

                }
            });
        }
    }

    /**
     * 金电手环，与SE交互，发送APDU指令
     * @param byteofdata
     * @param length
     */
    public void communicateWithJDSe(final byte[] byteofdata,final int length){
        int packageNum=0;           //包序号
        int total_package;      //总包数
        //计算total_package
        if(length<=14)
            total_package = 1;
        else {
            int mod = (length - 14) % 19; //蓝牙最后一包是否为20byte
            total_package =(length - 14) / 19;
            if(mod>0){
                total_package+=2;
            }
            else
                total_package+=1;
        }
        packages = total_package;
        byte[] header = new byte[6];
        header[0] = (byte)(((byte)(total_package << 4)) | ((byte)(packageNum))) ;
        header[1] = APDU_SEND;
        header[2] = (byte)((length + 2)>>8);
        header[3] = (byte)((length+2) & 0xFF);
        header[4] = (byte)(length >> 8);
        header[5] = (byte)(length & 0xFF);
        //当APDU指令包<14字节，则总包数为1包
        if(length<=14) {
            byte[] data = new byte[6+length];
            System.arraycopy(header,0,data,0,6);
            System.arraycopy(byteofdata,0,data,6,length);
            writeCharacteristic.setValue(data);
            bluetoothService.wirteCharacteristic(writeCharacteristic);
            LogUtil.debug(TAG,"send to BLE:"+ByteUtil.bytesToHexString(data,data.length));
            //apduLength = 0;
            //apduSentLength = length;
        }
        else{
            //当APDU指令包>14字节，总包数大于1包
            final byte[] data = new byte[20];
            final int mod = (length - 14) % 19; //蓝牙最后一包是否为20byte
            System.arraycopy(header,0,data,0,6);
            System.arraycopy(byteofdata,0,data,6,14);
            writeCharacteristic.setValue(data);
            bluetoothService.wirteCharacteristic(writeCharacteristic);
            bluetoothService.bWriteCharacteristic=false;
            LogUtil.debug(TAG,"send to BLE:"+ByteUtil.bytesToHexString(data,data.length));
            //apduLength -= 14;
            apduSentLength = 14;
            packages-=1;
            bluetoothService.setBleCallbackListener(new BluetoothService.BLECallbackListener() {
                @Override
                public void onResponseWrite(int para) {
                    byte firstByte = (byte)para;
                    int totalPackage = (firstByte & 0xF0)>>4;
                    int packageNo = (firstByte & 0x0F);
                    if((packages>1) || ((packages==1) && (mod==0))){
                        //packageNum+=1;
                        data[0]=(byte)(((byte)(totalPackage << 4)) | ((byte)(packageNo+1))) ;
                        System.arraycopy(byteofdata,apduSentLength,data,1,19);
//                        //确保上次写完成
//                        while (!bluetoothService.bWriteCharacteristic) {
//
//                        }
                        writeCharacteristic.setValue(data);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
//                        bluetoothService.bWriteCharacteristic=false;
                        LogUtil.debug(TAG,"send to BLE:"+ByteUtil.bytesToHexString(data,data.length));
                        //apduLength -= 19;
                        apduSentLength +=19;
                        packages-=1;
                    }
                    else if((packages==1) && (mod!=0)){
                        byte[] lastPackage = new byte[mod+1];
                        //packageNum+=1;
                        lastPackage[0]=(byte)(((byte)(totalPackage << 4)) | ((byte)(packageNo+1))) ;
                        System.arraycopy(byteofdata,apduSentLength,lastPackage,1,mod);
                        //确保上次写完成
//                        while (!bluetoothService.bWriteCharacteristic) {
//
//                        }
                        writeCharacteristic.setValue(lastPackage);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
//                        bluetoothService.bWriteCharacteristic=false;
                        LogUtil.debug(TAG,"send to BLE:"+ByteUtil.bytesToHexString(lastPackage,lastPackage.length));
//                        apduLength-=mod;
                        apduSentLength+=mod;
                        packages-=1;
                        if(apduSentLength==length)
                        {
                            //apduSentLength=0;
                            LogUtil.debug("apdu send success!");
                        }
                        else
                            LogUtil.debug("apdu send failed!");
                    }

                }

                @Override
                public void onResponseRead(int receiveTime) {

                }
            });
        }

    }

    /**
     * 展讯蓝牙，与SE交互，发送APDU指令
     * @param byteofdata
     * @param length
     */
    public void communicateWithSe(final byte []byteofdata,final int length){
        if(notifyCharacteristic==null || writeCharacteristic==null)
            return;
        //bluetoothService.setCharacteristicNotification(notifyCharacteristic,true);
        final byte []sendData = new byte[BLE_MTU];
        final int mod = length%BLE_MTU_DATA;
        //int sendTime;
        sendCount=0;
        if(mod>0)
            sendTime = length/BLE_MTU_DATA+1;
        else
            sendTime = length/BLE_MTU_DATA;
        //int sendCount=0;
        //给BLE发送数据
        sendData[0]=(byte)(sendTime-1);
        if(length>BLE_MTU_DATA)
            System.arraycopy(byteofdata,sendCount,sendData,1,BLE_MTU_DATA);
        else
            System.arraycopy(byteofdata,sendCount,sendData,1,length);
        //sendCount+=19;
        writeCharacteristic.setValue(sendData);
        bluetoothService.wirteCharacteristic(writeCharacteristic);
        LogUtil.debug(TAG,"send to BLE:"+ ByteUtil.bytesToHexString(sendData,sendData.length));
        //给蓝牙发送数据，倒计时开始
        bluetoothService.countDowntimer.start();
        bluetoothService.countDowntimer.setCountDownTimerListerner(new MyCountDowntimer.CountDownTimerListener() {
            @Override
            public void onTimeup() {
                //重发时间到
                bluetoothService.wirteCharacteristic(writeCharacteristic);
            }
        });
        bluetoothService.setBleCallbackListener(new BluetoothService.BLECallbackListener() {
            @Override
            public void onResponseWrite(int receiveTime) {
                if(receiveTime == (sendTime-1)){
                    if(receiveTime==0x0)
                        return;
                    sendTime-=1;
                    sendCount+=BLE_MTU_DATA;
                    if ((sendTime>1 && mod>0) || (sendTime>0 && mod==0)){
                        sendData[0]=(byte)(sendTime-1);
                        System.arraycopy(byteofdata,sendCount,sendData,1,BLE_MTU_DATA);
                        writeCharacteristic.setValue(sendData);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
                        LogUtil.debug(TAG,"onResponseWrite send to BLE:"+ByteUtil.bytesToHexString(sendData,sendData.length));
                        //给蓝牙发送数据，倒计时开始
                        bluetoothService.countDowntimer.start();
                        bluetoothService.countDowntimer.setCountDownTimerListerner(new MyCountDowntimer.CountDownTimerListener() {
                            @Override
                            public void onTimeup() {
                                //重发时间到
                                bluetoothService.wirteCharacteristic(writeCharacteristic);
                            }
                        });
                    }
                    else
                    {
                        //sendTime=0,mod>0,剩余字节数不足19,重新定义缓冲区
                        byte []data=new byte[length-sendCount+1];
                        data[0]=(byte)0;
                        System.arraycopy(byteofdata,sendCount,data,1,length-sendCount);
                        writeCharacteristic.setValue(data);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
                        LogUtil.debug(TAG,"onResponseWrite send to BLE:"+ByteUtil.bytesToHexString(data,data.length));
                    }
                }
            }
            //给蓝牙的2字节应答
            @Override
            public void onResponseRead(int receiveTime) {
                byte []responseDataToBle = new byte[2];
                responseDataToBle[0]=(byte)receiveTime;
                responseDataToBle[1]=(byte)(~((byte)receiveTime));
                writeCharacteristic.setValue(responseDataToBle);
                bluetoothService.wirteCharacteristic(writeCharacteristic);
                LogUtil.debug(TAG,"onResponseRead send to BLE:"+ByteUtil.bytesToHexString(responseDataToBle,responseDataToBle.length));
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static String TAG = "BLE";

    public static int BLE_MTU = 20;
    public static int BLE_MTU_DATA = 19;
}
