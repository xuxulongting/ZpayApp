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
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.security.PublicKey;
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
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
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
                SelectBluetoothDeviceListener listener = (SelectBluetoothDeviceListener) getActivity();
                listener.onBluetoothDeviceSelected(btDeviceAddressList.get(i));
                bluetoothStopDiscover();
            }
        });
        commonAdapter = new CommonAdapter<String>(view.getContext(),R.layout.item_list_btdevices, btDevicesNameList) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_tv_bt_device_name,item);
            }

        };
        listView.setAdapter(commonAdapter);



        return new AlertDialog.Builder(getActivity())
                .setTitle("正在查找蓝牙设备...")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
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

    public BluetoothReceiver bluetoothReceiver=null;

    public BluetoothReceiver getBluetoothReceiverInstance(){
        if(bluetoothReceiver==null){
            return new BluetoothReceiver();
        }
        else
            return bluetoothReceiver;
    }
}
