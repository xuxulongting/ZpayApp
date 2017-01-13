package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.ActivityManager;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.common.MySharedPreference;
import com.spreadtrum.iit.zpayapp.network.NetParameter;
import com.spreadtrum.iit.zpayapp.network.ResultCallback;
import com.spreadtrum.iit.zpayapp.network.ZAppStoreApi;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothSettingsActivity;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.register_login.DigtalpwdLoginActivity;
import com.spreadtrum.iit.zpayapp.register_login.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SPREADTRUM\ting.long on 16-9-5.
 */
public class SettingsFragment extends Fragment {
    private Button btnAccountSafe;
    private Button btnBluetooth;
    private Button btnAboutUs;
    private Button btnQuit;
    private Button btnCloseBLE;
    public interface SelectBluetoothDeviceListener{
        void onBluetoothDeviceSelected(String devAddr);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
//        //帐号与安全
//        btnAccountSafe = (Button) view.findViewById(R.id.id_btn_account_safe);
//        btnAccountSafe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), DigtalpwdLoginActivity.class);
//                startActivity(intent);
//            }
//        });
        //蓝牙设置
        btnBluetooth = (Button) view.findViewById(R.id.id_btn_ble_settings);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BluetoothSettingsActivity.class);
                startActivityForResult(intent,REQUEST_BLUETOOTH_DEVICE);
            }
        });
        //关于我们
        btnAboutUs = (Button) view.findViewById(R.id.id_btn_about_us);
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AboutActivity.class);
                startActivity(intent);
            }
        });
        //关闭蓝牙
        btnCloseBLE = (Button) view.findViewById(R.id.id_btn_close_ble);
//        btnCloseBLE.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyApplication app = (MyApplication) MyApplication.getContextObject();
//                BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
//                        app.getBluetoothDevAddr());
//                if (bluetoothControl!=null)
//                    bluetoothControl.disconnectBluetooth();
////                    bluetoothControl.bluetoothUnbindService();
//            }
//        });
        //退出登录
        btnQuit = (Button) view.findViewById(R.id.id_btn_quit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogout();
            }
        });
        return view;
    }

    /**
     * 退出登录
     */
    private void userLogout(){
        //获取token
        String token = getToken();
        //获取version
        MyApplication app = MyApplication.getInstance();
        PackageInfo info = app.getPackageInfo();
        String versionName = info.versionName;
        //获取User
        UserInfo userInfo = MySharedPreference.getUserInfo(MyApplication.getContextObject());
        String user = userInfo.getLoginName();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version",versionName);
            jsonObject.put("logName",user);
            jsonObject.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ZAppStoreApi.logout(user, token, versionName, new ResultCallback<JSONObject>() {
            @Override
            public void onPreStart() {

            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    final String result = response.getString("result");
                    Observable.just(result)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    if(s.equals("0")){
//                                                    dialog.dismiss();
                                        Toast.makeText(MyApplication.getContextObject(),"退出失败",Toast.LENGTH_LONG).show();
                                    }
                                    else {
//                                                    dialog.dismiss();
                                        Toast.makeText(MyApplication.getContextObject(),"退出成功",Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(getActivity(),DigtalpwdLoginActivity.class);
                                        startActivity(intent);
                                        ActivityManager.getInstance().finishAllActivity();
                                    }
                                }
                            });
                } catch (JSONException e) {
                    LogUtil.debug("userLogout JSONException"+e.getMessage());
//                                e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String error) {
                Observable.just(0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
//                                    dialog.dismiss();
                                Toast.makeText(MyApplication.getContextObject(),"退出失败",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    /**
     * 退出登录
     */
//    private void userLogout1(){
//        //获取token
//        String token = getToken();
//        if(token.equals("")){
//            Toast.makeText(MyApplication.getContextObject(),"您还没有登录",Toast.LENGTH_LONG).show();
//            return;
//        }
////        final AlertDialog dialog = new AlertDialog.Builder(MyApplication.getContextObject())
////                .setMessage("正在退出，请稍候...").show();
//        JSONObject jsonObject = new JSONObject();
//        try {
//            //获取App版本信息
//            MyApplication app = MyApplication.getInstance();
////            JSONObject jsonAppInfo = app.getAppInfo();
////            String versionName = jsonAppInfo.getString("versionName");
//            PackageInfo info = app.getPackageInfo();
//            String versionName = info.versionName;
//            //获取logName
//            UserInfo userInfo = MySharedPreference.getUserInfo(MyApplication.getContextObject());
//            String user = userInfo.getLoginName();
//            jsonObject.put("version",versionName);
//            jsonObject.put("logName",user);
//            jsonObject.put("token",token);
//            final RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetParameter.LOGOUT_URL, jsonObject,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(final JSONObject response) {
//                            try {
//                                final String result = response.getString("result");
//                                Observable.just(result)
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(new Action1<String>() {
//                                            @Override
//                                            public void call(String s) {
//                                                if(s.equals("0")){
////                                                    dialog.dismiss();
//                                                    Toast.makeText(MyApplication.getContextObject(),"退出失败",Toast.LENGTH_LONG).show();
//                                                }
//                                                else {
////                                                    dialog.dismiss();
//                                                    Toast.makeText(MyApplication.getContextObject(),"退出成功",Toast.LENGTH_LONG).show();
//
//                                                    Intent intent = new Intent(getActivity(),DigtalpwdLoginActivity.class);
//                                                    startActivity(intent);
//                                                    ActivityManager.getInstance().finishAllActivity();
//                                                }
//                                            }
//                                        });
//                            } catch (JSONException e) {
//                                LogUtil.debug("userLogout JSONException"+e.getMessage());
////                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Observable.just(0)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Action1<Integer>() {
//                                @Override
//                                public void call(Integer integer) {
////                                    dialog.dismiss();
//                                    Toast.makeText(MyApplication.getContextObject(),"退出失败",Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                }
//            });
//            requestQueue.add(jsonObjectRequest);
//        } catch (JSONException e) {
////            e.printStackTrace();
//            LogUtil.debug("userLogout JSONException"+e.getMessage());
//        }
//
//
//
//    }

    /**
     * 从SharedPreferences中获取token值
     * @return
     */
    private String getToken(){
        String token="";
        SharedPreferences pref = getActivity().getSharedPreferences("token",MODE_PRIVATE);
        if(pref!=null){
            token = pref.getString("token","");
        }
        return token;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.debug("onActivityResult--SettingsFragment");
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
//    public static final String LOGOUT_URL = "";
}
