package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothSettingsActivity;


/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class MainDisplayActivity extends AppCompatActivity implements View.OnClickListener,SettingsFragment.SelectBluetoothDeviceListener {
    private Button btnAppStore;
    private Button btnAppService;
    private Button btnAppLocal;
    private Button btnAppSettings;
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
        AppStoreFragment appStoreFragment = new AppStoreFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.id_main,appStoreFragment,"AppStoreFragment").commit();
        setButtonColor(Color.GREEN);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_app_store:
                getFragment(FRAGMENT_APP_STORE);
                setButtonColor(FRAGMENT_APP_STORE);
                break;
            case R.id.id_btn_service:
                break;
            case R.id.id_btn_local:
                break;
            case R.id.id_btn_settings:
                getFragment(FRAGMENT_APP_SETTINGS);
                setButtonColor(FRAGMENT_APP_SETTINGS);
                break;
        }
    }

    public void setButtonColor(int btnPara){
        switch (btnPara){
            case FRAGMENT_APP_STORE:
                btnAppSettings.setTextColor(Color.WHITE);
                btnAppStore.setTextColor(Color.GREEN);
                break;
            case FRAGMENT_APP_SERVICE:
                break;
            case FRAGMENT_APP_LOCAL:
                break;
            case FRAGMENT_APP_SETTINGS:
                btnAppSettings.setTextColor(Color.GREEN);
                btnAppStore.setTextColor(Color.WHITE);
                break;
        }
    }

    public void getFragment(int para){
        FragmentManager fm = getFragmentManager();
        switch(para){
            case FRAGMENT_APP_STORE:
                AppStoreFragment appStoreFragment = new AppStoreFragment();
                fm.beginTransaction().replace(R.id.id_main,appStoreFragment,"AppStoreFragment").commit();
                break;
            case FRAGMENT_APP_SERVICE:
                break;
            case FRAGMENT_APP_LOCAL:
                break;
            case FRAGMENT_APP_SETTINGS:
                SettingsFragment settingsFragment = new SettingsFragment();
                fm.beginTransaction().replace(R.id.id_main,settingsFragment,"SettingsFragment").commit();
                break;
        }

    }



    public static final int FRAGMENT_APP_STORE=0;
    public static final int FRAGMENT_APP_SERVICE=1;
    public static final int FRAGMENT_APP_LOCAL=2;
    public static final int FRAGMENT_APP_SETTINGS=3;


    @Override
    public void onBluetoothDeviceSelected(String devAddr) {
        LogUtil.debug("bluetooth address:"+devAddr);
        //bluetoothDevAddr = devAddr;
        MyApplication app= (MyApplication) getApplication();
        app.setBluetoothDevAddr(devAddr);
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
}
