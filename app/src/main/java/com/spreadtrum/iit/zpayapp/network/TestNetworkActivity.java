package com.spreadtrum.iit.zpayapp.network;

import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spreadtrum.iit.zpayapp.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothDialogFragment;
import com.spreadtrum.iit.zpayapp.network.tcp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPSocket;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by SPREADTRUM\ting.long on 16-8-9.
 */
public class TestNetworkActivity extends AppCompatActivity implements BluetoothDialogFragment.SelectBluetoothDeviceListener{
    private Button btnTestNetwork;
    private Button btnSelBleDEv;
    private BluetoothControl bluetoothControl = null;
    BluetoothDialogFragment dialogFragment;
    private TCPSocket tcpSocket = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testnetwork);
        btnTestNetwork = (Button) findViewById(R.id.id_btn_interact);
        btnSelBleDEv = (Button) findViewById(R.id.id_btn_sel_ble);
        btnTestNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (tcpSocket == null) {
                            try {
                                tcpSocket = TCPSocket.getInstance(NetParameter.IPAddress, NetParameter.Port);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        String strSeId = "451000000000000020160328000000010003";
                        byte[] maskId = {0x12, (byte) 0xab};
                        //byte[] randomNum = {0x01,0x02,0x03,0x4,0x5,0x6,0x7,0x8,0x9,0xa,0xb,0xc};
                        byte[] cmd = {0x01, 0x00, 0x39, 0x24};
                        byte[] bSeNO = strSeId.getBytes();
                        byte[] id = {0x2E, 0x6E};
                        byte[] input = new byte[2 + 12 + 1 + 2 + 57];
                        byte[] taskId = new byte[20];
                        System.arraycopy(id, 0, taskId, 18, id.length);
                        System.arraycopy(maskId, 0, input, 0, maskId.length);
                        System.arraycopy(randomNum, 0, input, maskId.length, randomNum.length);
                        System.arraycopy(cmd, 0, input, maskId.length + randomNum.length, cmd.length);
                        System.arraycopy(bSeNO, 0, input, maskId.length + randomNum.length + cmd.length, bSeNO.length);
                        System.arraycopy(taskId, 0, input, maskId.length + randomNum.length + cmd.length + bSeNO.length, taskId.length);
                        try {
                            tcpSocket.tcpSocketWrite(input);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] buf = new byte[100];
                        int readCount = tcpSocket.tcpSocketReadByte(buf);

                        int cmdParaLen = buf[15] << 8;
                        cmdParaLen = cmdParaLen + buf[16];
                        if (readCount == (cmdParaLen + 17))
                            LogUtil.debug(TAG, "TCP response correct");
                        else
                            LogUtil.debug(TAG, "TCP response error");
                        if(buf[14]==CMD_SERVER_APDU){
                            if(bluetoothControl!=null){
                                byte[] seData = new byte[readCount - 17];
                                System.arraycopy(buf, 17, seData, 0, readCount - 17);
                                bluetoothControl.communicateWithSe(seData);
                            }

                        }else if(buf[14]==CMD_SERVER_END){
                            return;

                        }else if(buf[14]==CMD_SERVER_RESET){

                        }
//                        if (bluetoothControl == null) {
//                            LogUtil.debug(TAG, "bluetoothControl is null");
//                            return;
//                        }
//                        byte[] seData = new byte[readCount - 17];
//                        System.arraycopy(buf, 17, seData, 0, readCount - 17);
//                        bluetoothControl.communicateWithSe(seData);
                        bluetoothControl.setSeResponseAvilableListerner(new BluetoothControl.SEResponseDataAvilableListener() {
                            @Override
                            public void onSeResponseDataAviable(byte[] responseData) {
                                LogUtil.debug(TAG,"onSeResponseDataAviable");
                                byte[] response = new byte[17+responseData.length];
                                response[0] = 0x12;
                                response[1] = (byte) 0xab;
                                System.arraycopy(randomNum, 0, response, 2, randomNum.length);
                                response[14]=CMD_SE_RESPONSE;
                                response[15]=(byte)(responseData.length>>8);
                                response[16]=(byte)(responseData.length);
                                System.arraycopy(responseData,0,response,17,responseData.length);
                                if (tcpSocket == null) {
                                    try {
                                        tcpSocket = TCPSocket.getInstance(NetParameter.IPAddress, NetParameter.Port);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    if (tcpSocket.tcpSocketWrite(response)) {
                                        byte[] buf = new byte[1024];
                                        int readCount = tcpSocket.tcpSocketReadByte(buf);
                                        int cmdParaLen = buf[15] << 8;
                                        cmdParaLen = cmdParaLen + buf[16];
                                        if (readCount == (cmdParaLen + 17)) {
                                            LogUtil.debug(TAG, "TCP response correct");
                                        }
                                        else {
                                            LogUtil.debug(TAG, "TCP response error");
                                            return;
                                        }
                                        if(buf[14]==CMD_SERVER_APDU){
                                            if(bluetoothControl!=null){
                                                byte[] seData = new byte[readCount - 17];
                                                System.arraycopy(buf, 17, seData, 0, readCount - 17);
                                                bluetoothControl.communicateWithSe(seData);
                                            }

                                        }else if(buf[14]==CMD_SERVER_END){
                                            return;

                                        }else if(buf[14]==CMD_SERVER_RESET){

                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                }).start();
            }
        });
        btnSelBleDEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new BluetoothDialogFragment();
                dialogFragment.show(getFragmentManager(), "BLE DialogFragment");
            }
        });

    }

    @Override
    public void onBluetoothDeviceSelected(String devAddr) {
        LogUtil.debug(TAG, devAddr);
        LogUtil.debug(TAG, "onBluetoothDeviceSelected: " + devAddr);
        bluetoothControl = new BluetoothControl(this, devAddr);
        dialogFragment.dismiss();
    }

    public static String TAG = "BLE";
    public static byte CMD_APP_REQUEST = 0x01;
    public static byte CMD_SERVER_APDU = 0x02;
    public static byte CMD_SE_RESPONSE = 0x03;
    public static byte CMD_SERVER_END = 0x04;
    public static byte CMD_SERVER_RESET = 0x05;
    public byte[] randomNum = {0x01, 0x02, 0x03, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc};

    private void generateRandomData() {
        randomNum = new byte[12];
    }
}
