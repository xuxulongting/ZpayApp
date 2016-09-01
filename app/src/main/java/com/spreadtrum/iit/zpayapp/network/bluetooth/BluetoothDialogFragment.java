package com.spreadtrum.iit.zpayapp.network.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-8-9.
 */
public class BluetoothDialogFragment extends DialogFragment {
    private BluetoothAdapter btAdapter;
    private List<String> btDevicesNameList = new ArrayList<>();
    private List<String> btDeviceAddressList = new ArrayList<>();
    private CommonAdapter commonAdapter;

    public String TAG = "DIALOGFRAGMENT";

    public interface SelectBluetoothDeviceListener{
        void onBluetoothDeviceSelected(String devAddr);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        //LogUtil.debug(TAG,"onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        //LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        bluetoothReceiver = new BluetoothReceiver();
        getActivity().registerReceiver(bluetoothReceiver,intentFilter);
        //LogUtil.debug(TAG,"onActivityCreated");
        bluetoothEnable();
        bluetoothGetVisible();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //LogUtil.debug(TAG,"onCreateDialog");
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_bluetooth,null);
        ListView listView = (ListView) view.findViewById(R.id.id_lv_bledev);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),btDevicesNameList.get(i),Toast.LENGTH_LONG).show();
                SelectBluetoothDeviceListener listener = (SelectBluetoothDeviceListener) getActivity();
                //回调
                listener.onBluetoothDeviceSelected(btDeviceAddressList.get(i));
                //停止发现蓝牙设备
                bluetoothStopDiscover();
            }
        });
        commonAdapter = new CommonAdapter<String>(view.getContext(),R.layout.item_list_btdevices, btDeviceAddressList) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_tv_bt_device_name,item);
            }

        };
        listView.setAdapter(commonAdapter);



        return new AlertDialog.Builder(getActivity())
                .setTitle("正在查找蓝牙设备...")
                .setView(view)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //停止发现蓝牙设备
//                        bluetoothStopDiscover();
//                    }
//                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //停止发现蓝牙设备
                        bluetoothStopDiscover();
                    }
                })
                .create();
    }

    public void unRegisterReceiverBLEFound(){
        //取消注册Receiver
        //getActivity().unregisterReceiver();
        //unregisterReceiver(bluetoothReceiver);
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

    public void bluetoothEnable(){
        if(btAdapter==null)
            return;
        if(!btAdapter.enable()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getActivity(),"Turned on",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getActivity(),"Already Turn on",Toast.LENGTH_LONG).show();
        }
    }

    public void bluetoothDisable(){
        if(btAdapter==null)
            return;
        btAdapter.disable();
        return;
    }

    private class BluetoothReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.debug(TAG,"EXTRA_DEVICE");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            btDevicesNameList.add(device.getName());
            btDeviceAddressList.add(device.getAddress());
            commonAdapter.notifyDataSetChanged();
        }
    }

    private BluetoothReceiver bluetoothReceiver=null;

    public BluetoothReceiver getBluetoothReceiverInstance(){
        if(bluetoothReceiver==null){
            return new BluetoothReceiver();
        }
        else
            return bluetoothReceiver;
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.debug(TAG,"DialogFragment onDestroy");
        if(bluetoothReceiver!=null)
            getActivity().unregisterReceiver(bluetoothReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
