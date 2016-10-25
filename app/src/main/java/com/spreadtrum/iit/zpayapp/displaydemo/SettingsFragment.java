package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothSettingsActivity;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.register_login.DigtalpwdLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SPREADTRUM\ting.long on 16-9-5.
 */
public class SettingsFragment extends Fragment {
    private Button btnAccountSafe;
    private Button btnBluetooth;
    private Button btnAboutUs;
    private Button btnQuit;
//    private BluetoothControl bluetoothControl=null;
//    private SelectBluetoothDeviceListener listener=null;//=new AppStoreFragment();
//
    public interface SelectBluetoothDeviceListener{
        void onBluetoothDeviceSelected(String devAddr);
    }
//
//    public void setBluetoothDeviceListener(SelectBluetoothDeviceListener listener){
//        this.listener = listener;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        //帐号与安全
        btnAccountSafe = (Button) view.findViewById(R.id.id_btn_account_safe);
        btnAccountSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DigtalpwdLoginActivity.class);
                startActivity(intent);
            }
        });
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

            }
        });
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
        if(token.equals("")){
            Toast.makeText(MyApplication.getContextObject(),"您还没有登录",Toast.LENGTH_LONG).show();
            return;
        }
        final AlertDialog dialog = new AlertDialog.Builder(MyApplication.getContextObject())
                .setMessage("正在退出，请稍候...").show();
        JSONObject jsonObject = new JSONObject();
        try {
            //获取App版本信息
            MyApplication app = MyApplication.getInstance();
            JSONObject jsonAppInfo = app.getAppInfo();
            String versionName = jsonAppInfo.getString("versionName");
            jsonObject.put("version",versionName);
            jsonObject.put("userId","123456");
            jsonObject.put("token",token);
            final RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGOUT_URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("result");
                                if(result.equals("0")){
                                    String errorCode = response.getString("errorCode");
                                    String errorMsg = response.getString("errorMsg");
                                    dialog.dismiss();
                                    Toast.makeText(MyApplication.getContextObject(),"退出失败",Toast.LENGTH_LONG).show();
                                }
                                else {

                                    dialog.dismiss();
                                    Toast.makeText(MyApplication.getContextObject(),"退出成功",Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                LogUtil.debug("userLogout JSONException"+e.getMessage());
//                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Toast.makeText(MyApplication.getContextObject(),"退出失败",Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtil.debug("userLogout JSONException"+e.getMessage());
        }

    }

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
    public static final String LOGOUT_URL = "";
}
