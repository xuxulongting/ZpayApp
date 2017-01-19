package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.displaydemo.AppStoreFragment;
import com.spreadtrum.iit.zpayapp.displaydemo.SettingsFragment;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.spreadtrum.iit.zpayapp.R.id.id_lv_bluetooth;

/**
 * Created by SPREADTRUM\ting.long on 16-9-5.
 */
public class BluetoothSettingsActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter;
    private List<String> btDevicesNameList = new ArrayList<>();
    private List<String> btDeviceAddressList = new ArrayList<>();
    private CommonAdapter commonAdapter;
    private BluetoothReceiver bluetoothReceiver = null;

    private TextView textViewRefresh = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_settings);
        textViewRefresh = (TextView) findViewById(R.id.id_tv_click_refresh);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        bluetoothReceiver = new BluetoothReceiver();
        //监听发现蓝牙设备
        registerReceiver(bluetoothReceiver,intentFilter);
        //打开蓝牙
        bluetoothEnable();
        //启动发现蓝牙设备
        bluetoothGetVisible();
        //在listview中显示
        final ListView listView = (ListView) findViewById(id_lv_bluetooth);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(BluetoothSettingsActivity.this,btDeviceAddressList.get(i),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("BLE_ADDR",btDeviceAddressList.get(i));
                setResult(SettingsFragment.RESULT_BLUETOOTH_DEVICE,intent);
                //停止发现蓝牙设备
                bluetoothStopDiscover();
                //销毁该activity
                finish();
            }
        });

        commonAdapter = new CommonAdapter<String>(this,R.layout.item_list_btdevices,btDevicesNameList) {

            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_tv_bt_device_name,item);
                viewHolder.setText(R.id.id_tv_bt_device_addr,btDeviceAddressList.get(position));
            }
        };
        listView.setAdapter(commonAdapter);

        textViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动发现蓝牙设备
                btDevicesNameList.clear();
                btDeviceAddressList.clear();
                bluetoothGetVisible();
            }
        });
    }

    private class BluetoothReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            btDeviceAddressList.add(bluetoothDevice.getAddress());
            btDevicesNameList.add(bluetoothDevice.getName());
            commonAdapter.notifyDataSetChanged();
        }
    }

    public void bluetoothEnable(){
        if(btAdapter==null)
            return;
        if(!btAdapter.enable()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(this,"Turned on",Toast.LENGTH_LONG).show();
        }
        else{
//            Toast.makeText(this,"Already Turn on",Toast.LENGTH_LONG).show();
        }
    }

    public void bluetoothDisable(){
        if(btAdapter==null)
            return;
        btAdapter.disable();
        return;
    }

    public void bluetoothGetVisible(){
        if(btAdapter==null)
            return;
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible,0);
        btAdapter.startDiscovery();
    }

    public void bluetoothStopDiscover(){
        if (btAdapter==null)
            return;
        btAdapter.cancelDiscovery();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(bluetoothReceiver!=null)
//            unregisterReceiver(bluetoothReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothReceiver!=null)
            unregisterReceiver(bluetoothReceiver);
    }
}
