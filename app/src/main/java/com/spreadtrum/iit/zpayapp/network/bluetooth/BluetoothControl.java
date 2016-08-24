package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.LogUtil;

import java.util.ArrayList;
import java.util.List;


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

    private int sendTime=0;
    private int sendCount=0;

    private SEResponseDataAvilableListener listener;

    public interface SEResponseDataAvilableListener{
        void onSeResponseDataAviable(byte []responseData);
    }

    public static String SE_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static String NOTIFY_CHARACTERISTIC = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static String WRITE_CHARACTERISTIC = "0003cdd2-0000-1000-8000-00805f9b0131";
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bluetoothService = ((BluetoothService.LocalBinder) iBinder)
                    .getService();
            if(bluetoothService.initialize()==false){
                Toast.makeText(mContext,"initialize failed",Toast.LENGTH_SHORT).show();
            }
            //注册receiver
            mContext.registerReceiver(gattUpdateReceiver,makeGattUpdateIntentFilter());

            bluetoothService.connect(selBluetoothDevAddr);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //关闭连接 gatt can only handle sevral connections at a time (BluetoothGatt documentation)
            if(bluetoothService!=null) {
                bluetoothService.closeBluetoothGatt();
                bluetoothService = null;
            }

        }
    };

    public void setSeResponseAvilableListerner(SEResponseDataAvilableListener listerner){
        this.listener = listerner;
    }

    public void setSeCallbackTSMListener(SECallbackTSMListener listener){
        if(bluetoothService!=null){
            bluetoothService.setSeCallbackTSMListener(listener);
        }
    }

    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action){
                case BluetoothService.ACTION_GATT_CONNECTED:
                    break;
                case BluetoothService.ACTION_GATT_DISCONNECTED:
                    bluetoothService.connect(selBluetoothDevAddr);
                    break;
                case BluetoothService.ACTION_GATT_SERVICES_DISCOVERED:
                    gattServiceList = bluetoothService.getSupportedGattServices();
                    seCommService = getSpecialGattService(gattServiceList,SE_SERVICE);
                    notifyCharacteristic = getSpecialCharacteristic(seCommService,NOTIFY_CHARACTERISTIC);
                    writeCharacteristic = getSpecialCharacteristic(seCommService,WRITE_CHARACTERISTIC);
                    bluetoothService.setCharacteristicNotification(notifyCharacteristic,true);
                    getAuthentication(writeCharacteristic,"123456");
                    LogUtil.debug(TAG,"WRITE PWD");
                    break;
                case BluetoothService.ACTION_DATA_AVAILABLE:
                    byte []responseData = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                    int responseDataLen = responseData.length;
                    //LogUtil.debug(TAG,"ACTION_DATA_AVAILABLE: "+responseDataLen);
                    LogUtil.debug(TAG,bytesToHexString(responseData));
                    //
                    //listener.onSeResponseDataAviable(responseData);
                    //callbackTSMListener.callbackTSM(responseData);
                    break;
            }
        }
    };

    public BluetoothControl(Context context,String selBleDevAddr){
        this.mContext = context;
        this.selBluetoothDevAddr = selBleDevAddr;
        Intent intent = new Intent(context,BluetoothService.class);
        boolean bBind = context.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
        if(bBind==false){
            Toast.makeText(context,"bind service failed",Toast.LENGTH_SHORT).show();
        }
    }

    public void bluetoothUnregisterReceiver(){

        mContext.unregisterReceiver(gattUpdateReceiver);
    }

    public void bluetoothUnbindService()
    {
        mContext.unbindService(serviceConnection);

        bluetoothService = null;
    }

    public BluetoothGattService getSpecialGattService(List<BluetoothGattService> listGattService,String strUuid){
        for(BluetoothGattService gattService : listGattService){
            if(gattService.getUuid().toString().equals(strUuid))
                return gattService;
        }
        return null;
    }

    public BluetoothGattCharacteristic getSpecialCharacteristic(BluetoothGattService gattService,String strUuid){
        List<BluetoothGattCharacteristic> gattCharacteristicList = gattService.getCharacteristics();
        for(BluetoothGattCharacteristic gattCharacteristic :gattCharacteristicList){
            if(gattCharacteristic.getUuid().toString().equals(strUuid))
                return gattCharacteristic;
        }
        return null;
    }

    public void getAuthentication(BluetoothGattCharacteristic gattCharacteristic,String pwd){
        gattCharacteristic.setValue(pwd.getBytes());
        bluetoothService.wirteCharacteristic(gattCharacteristic);
    }

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
        LogUtil.debug(TAG,"send to BLE:"+bytesToHexString(sendData));
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
                        LogUtil.debug(TAG,"onResponseWrite send to BLE:"+bytesToHexString(sendData));
                    }
                    else
                    {
                        //sendTime=0,mod>0,剩余字节数不足19,重新定义缓冲区
                        byte []data=new byte[length-sendCount+1];
                        data[0]=(byte)0;
                        System.arraycopy(byteofdata,sendCount,data,1,length-sendCount);
                        writeCharacteristic.setValue(data);
                        bluetoothService.wirteCharacteristic(writeCharacteristic);
                        LogUtil.debug(TAG,"onResponseWrite send to BLE:"+bytesToHexString(data));
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
                LogUtil.debug(TAG,"onResponseRead send to BLE:"+bytesToHexString(responseDataToBle));
            }
        });


//        while((sendTime>1 && mod>0) || (sendTime>0 && mod==0)){
//            sendData[0]=(byte)(sendTime-1);
//            System.arraycopy(byteofdata,sendCount,sendData,1,19);
//            sendCount+=19;
//            writeCharacteristic.setValue(sendData);
//            bluetoothService.wirteCharacteristic(writeCharacteristic);
//            sendTime-=1;
//            LogUtil.debug(TAG,"send to BLE:"+bytesToHexString(sendData));
//        }
//        //sendTime=0,mod>0,剩余字节数不足19,重新定义缓冲区
//        if(sendTime==0){
//            byte []data=new byte[length-sendCount+1];
//            data[0]=(byte)0;
//            System.arraycopy(byteofdata,sendCount,data,1,length-sendCount);
//            writeCharacteristic.setValue(data);
//            bluetoothService.wirteCharacteristic(writeCharacteristic);
//            LogUtil.debug(TAG,"send to BLE:"+bytesToHexString(data));
//        }
//        writeCharacteristic.setValue(byteofdata);
//        bluetoothService.wirteCharacteristic(writeCharacteristic);
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

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    public static String TAG = "BLE";

    public static int BLE_MTU = 20;
    public static int BLE_MTU_DATA = 19;
}
