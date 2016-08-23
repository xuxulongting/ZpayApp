package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.LogUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.spreadtrum.iit.zpayapp.network.bluetooth.SampleGattAttributes.*;

/**
 * Created by SPREADTRUM\ting.long on 16-8-5.
 */
public class BluetoothService extends android.app.Service{

    public static final String ACTION_GATT_CONNECTED = "com.spreadtrum.iit.zpayapp.network.bluetooth.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.spreadtrum.iit.zpayapp.network.bluetooth.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.spreadtrum.iit.zpayapp.network.bluetooth.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "com.spreadtrum.iit.zpayapp.network.bluetooth.ACTION_DATA_AVAILABLE";
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public static final String TAG = "BLE";
    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID
            .fromString(HEART_RATE_MEASUREMENT);
    public static final String EXTRA_DATA = "com.spreadtrum.iit.zpayapp.network.bluetooth.EXTRA_DATA";


    private int connectionState;
    private BluetoothGatt bluetoothGatt;

    private BluetoothAdapter bluetoothAdapter;
    private String bluetoothDeviceAddress;
    private BluetoothManager bluetoothManager;

    private SECallbackTSMListener callbackTSMListener;

    private BLECallbackListener bleCallbackListener;

    private byte[] seResponseData = new byte[256];
    private int seDataLength=0;

    public interface BLECallbackListener{
        void onResponseWrite(int sendTime);
        void onResponseRead(int receiveTime);
    }

    public void setBleCallbackListener(BLECallbackListener listener){
        this.bleCallbackListener = listener;
    }

    public void setSeCallbackTSMListener(SECallbackTSMListener listener){
        this.callbackTSMListener = listener;
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            //System.out.println("=======status:" + status);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                //broadcastUpdate(intentAction);
                //修改MTU size
                //bluetoothGatt.requestMtu(103);
                LogUtil.info(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                LogUtil.info(TAG, "Attempting to start service discovery:"
                        + bluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                LogUtil.info(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //super.onServicesDiscovered(gatt, status);
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                LogUtil.info(TAG,"services discovered");
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//            } else {
//                Log.w(TAG, "onServicesDiscovered received: " + status);
//            }
//            if (status != 0) {
//                bluetoothAdapter.disable();
//
//                Timer single_timer = new Timer();
//                single_timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        bluetoothAdapter.enable();
//                    }
//                }, 1000);
//
//
//                Timer second_timer = new Timer();
//                second_timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        //gattServiceIntent = new Intent(context, BluetoothLeService.class);
//                        //bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//                        //mBluetoothLeService.initialize();
//                        //mBluetoothLeService.connect(mDeviceAddress);
//                        initialize();
//                        connect()
//                        //maybe you need wait 0.5-1 second to call connect() after called initialize()
//                    }
//                }, 2000);
//
//
//                Log.e(TAG, "An error code get at onServicesDiscovered= " + status);
//            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                LogUtil.warn(TAG, "onServicesDiscovered received: " + status);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                bluetoothGatt.discoverServices();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicWrite(gatt, characteristic, status);
            //System.out.println("--------write success----- status:" + status);
            LogUtil.info(TAG,"--------write Characteristic success----- status:" + status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicRead(gatt, characteristic, status);
            LogUtil.info(TAG,"--------read Characteristic----- status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //super.onCharacteristicChanged(gatt, characteristic);
            LogUtil.info(TAG,"--------onCharacteristicChanged-----");

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//            if (characteristic.getValue() != null) {
//
//                LogUtil.debug(TAG,characteristic.getStringValue(0));
//            }

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //super.onDescriptorWrite(gatt, descriptor, status);
            LogUtil.debug(TAG,"onDescriptorWrite = " + status
                    + ", descriptor =" + descriptor.getUuid().toString());
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };


    public class LocalBinder extends Binder {
        BluetoothService getService(){
            return BluetoothService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IBinder binder = new LocalBinder();

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            System.out.println("Received heart rate: %d" + heartRate);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            LogUtil.debug(TAG,"receive from ble:"+bytesToHexString(data));
            //判断是SE返回的数据，还是蓝牙响应数据
            //收到蓝牙的2字节应答数据
            if(data.length==2){
                if(data[0]==((byte)(~data[1]))) {
                    //LogUtil.debug(TAG,"BLE response data");
                    bleCallbackListener.onResponseWrite((int)data[0]);
                    return;
                }
            }
            //SE返回数据
            System.arraycopy(data,1,seResponseData,seDataLength,data.length-1);
            seDataLength+=(data.length-1);
            if(data[0]!=0x0){
                //给蓝牙发送2字节应答数据
                bleCallbackListener.onResponseRead((int)data[0]);
            }
            else{
                //LogUtil.debug(TAG,"callbackTSM:"+bytesToHexString(seResponseData));
                //给蓝牙发送2字节应答数据
                bleCallbackListener.onResponseRead(0);
                //将数据发送给TSM处理
                callbackTSMListener.callbackTSM(seResponseData,seDataLength);
                //将长度清零
                seDataLength=0;
                //将byte数组清零
                //Arrays.fill(seResponseData,(byte) 0);
            }

//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(
//                        data.length);
//                for (byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//
//                //System.out.println("ppp" + new String(data) + "\n" + stringBuilder.toString());
//                //LogUtil.debug(TAG,new String(data) + "\n" + stringBuilder.toString());
//                //intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
//                //intent.putExtra(EXTRA_DATA,data);
//
//                //callbackTSMListener.callbackTSM(data);
//
//            }
        }
        //sendBroadcast(intent);
    }

    public void seResponseDataToZero()
    {
        Arrays.fill(seResponseData,(byte)0);
        seDataLength = 0;
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

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
            return false;
        }
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }



//    public void scanBLEDevice(){
//        Intent scanDevice = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        startActivityForResult(scanDevice);
//    }

    public void stopBLEDevice(){

    }


    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address
     *            The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The
     *         connection result is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        LogUtil.info(TAG,"CONNECTING... BY LONG 2016-8-5");
        // Previously connected device. Try to reconnect. (\CF\C8ǰ\C1\AC\BDӵ\C4\C9豸\A1\A3 \B3\A2\CA\D4\D6\D8\D0\C2\C1\AC\BD\D3)
        if (bluetoothDeviceAddress != null
                && address.equals(bluetoothDeviceAddress)
                && bluetoothGatt != null) {
            Log.d(TAG,
                    "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                connectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        bluetoothDeviceAddress = address;
        connectionState = STATE_CONNECTING;

        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void closeBluetoothGatt() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        bluetoothGatt.writeCharacteristic(characteristic);

    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic
     *            The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic
     *            Characteristic to act on.
     * @param enabled
     *            If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            System.out.println("write descriptor");

            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
		/*
		 * // This is specific to Heart Rate Measurement. if
		 * (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		 * System
		 * .out.println("characteristic.getUuid() == "+characteristic.getUuid
		 * ()+", "); BluetoothGattDescriptor descriptor =
		 * characteristic.getDescriptor
		 * (UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		 * descriptor
		 * .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		 * mBluetoothGatt.writeDescriptor(descriptor); }
		 */
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null)
            return null;

        return bluetoothGatt.getServices();
    }

    /**
     * Read the RSSI for a connected remote device.
     * */
    public boolean getRssiVal() {
        if (bluetoothGatt == null)
            return false;

        return bluetoothGatt.readRemoteRssi();
    }



}
