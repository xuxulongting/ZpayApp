package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.PullToRefreshLayoutTellH.PullToRefreshLayout;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothSettingsActivity;



/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class MainDisplayActivity extends BaseActivity implements View.OnClickListener,
        SettingsFragment.SelectBluetoothDeviceListener{
    private Button btnAppStore;
    private Button btnAppService;
    private Button btnAppLocal;
    private Button btnAppSettings;
    private AppStoreFragment selectedFragment;
    //public static String bluetoothDevAddr="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindisplay);
        btnAppStore = (Button) findViewById(R.id.id_btn_app_store);
        btnAppService = (Button) findViewById(R.id.id_btn_service);
        btnAppLocal = (Button) findViewById(R.id.id_btn_local);
        btnAppSettings = (Button) findViewById(R.id.id_btn_settings);
        btnAppStore.setOnClickListener(this);
        btnAppService.setOnClickListener(this);
        btnAppLocal.setOnClickListener(this);
        btnAppSettings.setOnClickListener(this);
//        AppStoreFragment appStoreFragment = new AppStoreFragment();
//        FragmentManager fm = getFragmentManager();
//        fm.beginTransaction().replace(R.id.id_main,appStoreFragment,"AppStoreFragment").commit();
        getFragment(FRAGMENT_APP_STORE);
        setButtonColor(FRAGMENT_APP_STORE);
        LogUtil.debug("dataFromNet is "+MyApplication.dataFromNet);
    }

    /**
     * Fragment下方按钮的响应函数
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_app_store:
                setButtonColor(FRAGMENT_APP_STORE);
                getFragment(FRAGMENT_APP_STORE);

                break;
            case R.id.id_btn_service:
                setButtonColor(FRAGMENT_APP_SERVICE);
                getFragment(FRAGMENT_APP_SERVICE);

                break;
            case R.id.id_btn_local:
                setButtonColor(FRAGMENT_APP_LOCAL);
                getFragment(FRAGMENT_APP_LOCAL);

                break;
            case R.id.id_btn_settings:
                setButtonColor(FRAGMENT_APP_SETTINGS);
                getFragment(FRAGMENT_APP_SETTINGS);

                break;
        }
    }

    /**
     * 显示Fragment对应的按钮的颜色
     * @param btnPara   代表特定Fragment的参数
     */
    public void setButtonColor(int btnPara){
        switch (btnPara){
            case FRAGMENT_APP_STORE:
                btnAppSettings.setTextColor(Color.WHITE);
                btnAppService.setTextColor(Color.WHITE);
                btnAppLocal.setTextColor(Color.WHITE);
                btnAppStore.setTextColor(Color.GREEN);
                break;
            case FRAGMENT_APP_SERVICE:
                btnAppSettings.setTextColor(Color.WHITE);
                btnAppService.setTextColor(Color.GREEN);
                btnAppLocal.setTextColor(Color.WHITE);
                btnAppStore.setTextColor(Color.WHITE);
                break;
            case FRAGMENT_APP_LOCAL:
                btnAppSettings.setTextColor(Color.WHITE);
                btnAppService.setTextColor(Color.WHITE);
                btnAppLocal.setTextColor(Color.GREEN);
                btnAppStore.setTextColor(Color.WHITE);
                break;
            case FRAGMENT_APP_SETTINGS:
                btnAppSettings.setTextColor(Color.GREEN);
                btnAppService.setTextColor(Color.WHITE);
                btnAppLocal.setTextColor(Color.WHITE);
                btnAppStore.setTextColor(Color.WHITE);
                break;
        }
    }

    /**
     * 显示Fragment
     * @param para  代表特定Fragment的参数
     */
    public void getFragment(int para){
        FragmentManager fm = getFragmentManager();
        switch(para){
            case FRAGMENT_APP_STORE:
                AppStoreFragment appStoreFragment = new AppStoreFragment();
                fm.beginTransaction().replace(R.id.id_main,appStoreFragment,"AppStoreFragment").commit();
                break;
            case FRAGMENT_APP_SERVICE:
                AppServiceFragment appServiceFragment = new AppServiceFragment();
                fm.beginTransaction().replace(R.id.id_main,appServiceFragment,"AppServiceFragment").commit();
                break;
            case FRAGMENT_APP_LOCAL:
                AppLocalFragment appLocalFragment = new AppLocalFragment();
                fm.beginTransaction().replace(R.id.id_main,appLocalFragment,"AppLocalFragment").commit();
                break;
            case FRAGMENT_APP_SETTINGS:
                SettingsFragment settingsFragment = new SettingsFragment();
                fm.beginTransaction().replace(R.id.id_main,settingsFragment,"SettingsFragment").commit();
                break;
        }

    }
    /**
     * 设置蓝牙后的回调，将蓝牙设备地址写入全局变量
     * @param devAddr   蓝牙设备的地址
     */
    @Override
    public void onBluetoothDeviceSelected(String devAddr) {
        LogUtil.debug("bluetooth address:"+devAddr);
        //bluetoothDevAddr = devAddr;
        MyApplication app= (MyApplication) getApplication();
        app.setBluetoothDevAddr(devAddr);
    }

    @Override
    protected void onRestart() {
        LogUtil.debug("MainDisplayActivity onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
//        LogUtil.debug("MainDisplayActivity onResume");
//        selectedFragment.onBackPressed();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
//        selectedFragment.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.debug("MainDisplayActivity onPause");
        //将appLst存入数据库

    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.debug("MainDisplayActivity onStop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.debug("MainDisplayActivity onDestroy");

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
        LogUtil.debug("onActivityResult--");
        if(requestCode==SettingsFragment.REQUEST_BLUETOOTH_DEVICE){
            LogUtil.debug("onActivityResult");
            if(requestCode == REQUEST_BLUETOOTH_DEVICE && resultCode == RESULT_BLUETOOTH_DEVICE) {
                String bluetoothDevAddr = data.getStringExtra("BLE_ADDR");
                MyApplication app = (MyApplication) getApplication();
                app.setBluetoothDevAddr(bluetoothDevAddr);
            }
        }
    }
    public static final int FRAGMENT_APP_STORE=0;
    public static final int FRAGMENT_APP_SERVICE=1;
    public static final int FRAGMENT_APP_LOCAL=2;
    public static final int FRAGMENT_APP_SETTINGS=3;

    public static final int REQUEST_BLUETOOTH_DEVICE=1;
    public static final int RESULT_BLUETOOTH_DEVICE=2;

//    @Override
//    public void setFragment(AppStoreFragment fragmentBackHandler) {
//        this.selectedFragment = fragmentBackHandler;
//    }
//    public enum FragmentName {FRAGMENT_APP_STORE, FRAGMENT_APP_SERVICE,
//                FRAGMENT_APP_LOCAL,FRAGMENT_APP_SETTINGS};
}
