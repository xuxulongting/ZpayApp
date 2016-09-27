package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothSettingsActivity;

/**
 * Created by SPREADTRUM\ting.long on 16-9-5.
 */
public class SettingsFragment extends Fragment {
    private Button btnBluetooth;
    private BluetoothControl bluetoothControl=null;
    private SelectBluetoothDeviceListener listener=null;//=new AppStoreFragment();

    public interface SelectBluetoothDeviceListener{
        void onBluetoothDeviceSelected(String devAddr);
    }

    public void setBluetoothDeviceListener(SelectBluetoothDeviceListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        btnBluetooth = (Button) view.findViewById(R.id.id_btn_ble_settings);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BluetoothSettingsActivity.class);
                startActivityForResult(intent,REQUEST_BLUETOOTH_DEVICE);
            }
        });
        return view;
    }

    /**
     * 关闭BluetoothSettingsActivity后的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SettingsFragment.REQUEST_BLUETOOTH_DEVICE){
            LogUtil.debug("onActivityResult");
            if(requestCode == REQUEST_BLUETOOTH_DEVICE && resultCode == RESULT_BLUETOOTH_DEVICE) {
                String bluetoothDevAddr = data.getStringExtra("BLE_ADDR");
                MyApplication app = (MyApplication) getActivity().getApplication();
                app.setBluetoothDevAddr(bluetoothDevAddr);
            }
        }
    }

    public static final int REQUEST_BLUETOOTH_DEVICE=1;
    public static final int RESULT_BLUETOOTH_DEVICE=2;
}
