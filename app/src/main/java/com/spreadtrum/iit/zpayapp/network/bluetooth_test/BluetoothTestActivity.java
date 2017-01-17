package com.spreadtrum.iit.zpayapp.network.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
//import com.zhy.adapter.recyclerview.CommonAdapter;
//import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.abslistview.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by SPREADTRUM\ting.long on 16-8-4.
 */
public class BluetoothTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTurnOn;
    private Button btnTurnOff;
    private Button btnGetvisible;
    private Button btnListDevices;
    private BluetoothAdapter btAdapter;
    private RecyclerView btRecyclerView;
    private ListView btListView;
    private List<String> btDevicesNameList = new ArrayList<>();
    private List<String> bleDeviceAddressList = new ArrayList<>();
    private Set<BluetoothDevice> pairedDevices;
    private CommonAdapter commonAdapter;
    private EditText edit1;
    private String bleAddConnected;
    private BluetoothGatt bleGatt;
    private int connectionState = STATE_DISCONNECTED;
    //private IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //btRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_bluetooth);
        btListView = (ListView) findViewById(R.id.id_lv_devices);
        btnTurnOn = (Button) findViewById(R.id.id_btn_turnon);
        btnTurnOff = (Button) findViewById(R.id.id_btn_turnoff);
        btnListDevices = (Button) findViewById(R.id.id_btn_listdevices);
        btnGetvisible = (Button) findViewById(R.id.id_btn_getvisible);
        //edit1 = (EditText) findViewById(R.id.id_et_bluetooth);
        btnTurnOn.setOnClickListener(this);
        btnTurnOff.setOnClickListener(this);
        btnGetvisible.setOnClickListener(this);
        btnListDevices.setOnClickListener(this);

        btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String bleDevAddr = bleDeviceAddressList.get(i);
                String bleDevName = btDevicesNameList.get(i);
                if (bleDevAddr.isEmpty())
                    return;
                final Intent intent = new Intent(BluetoothTestActivity.this, DeviceControlActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, bleDevName);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, bleDevAddr);
                startActivity(intent);
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        commonAdapter = new CommonAdapter<String>(this,R.layout.item_list_btdevices, btDevicesNameList) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.id_tv_bt_device_name,s);
            }
        };
        btListView.setAdapter(commonAdapter);

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        registerReceiver(bluetoothReceiver,intentFilter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_turnon:
                bluetoothEnable();
                break;
            case R.id.id_btn_turnoff:
                break;
            case R.id.id_btn_getvisible:
                bluetoothGetVisible();
                break;
            case R.id.id_btn_listdevices:
                blueToothDevicesList();
                break;
        }
    }

    public void blueToothDevicesList(){
//        pairedDevices = btAdapter.getBondedDevices();
//
//        for(BluetoothDevice bt : pairedDevices){
//            btDevicesNameList.add(bt.getName());
//            Toast.makeText(this,"蓝牙设备1",Toast.LENGTH_LONG).show();
//        }
        if(btDevicesNameList.size()>0)
            Toast.makeText(this,"发现多个蓝牙设备",Toast.LENGTH_LONG).show();
        commonAdapter.notifyDataSetChanged();
    }

    public void bluetoothGetVisible(){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible,0);
        btAdapter.startDiscovery();
    }

    void bluetoothEnable(){
        if(!btAdapter.enable()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on"
                    ,Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"Already Turn on",Toast.LENGTH_LONG).show();
        }
    }


    private class BluetoothReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            LogUtil.debug(TAG,"onReceive");
            //接受intent
            String action = intent.getAction();
            //从intent取出远程蓝牙设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //edit1.append("find new device:"+device.getName()+"\n");//getAddress()+"\n");
            btDevicesNameList.add(device.getName()+"\n");
            bleDeviceAddressList.add(device.getAddress());
            commonAdapter.notifyDataSetChanged();
        }

    }

    public static final String TAG = "BLE";
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
}
