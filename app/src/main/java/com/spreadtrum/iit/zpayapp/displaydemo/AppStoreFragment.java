package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.database.DatabaseHandler;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.webservice.ApplyPersonalizationService;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.spreadtrum.iit.zpayapp.register_login.DigtalpwdLoginActivity;
import com.zhy.adapter.abslistview.CommonAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class AppStoreFragment extends Fragment {
    private CommonAdapter busAdapter= null;
//    private List<Card> listCardParameter = new ArrayList<Card>();
//    private List<AppInformation> appInformationList = new ArrayList<AppInformation>();
    private static List<AppInformation> appList=null;
    private Button btnOperaCard;
    private LinearLayout linearLayoutBar;
    private AppDisplayDatabaseHelper dbHelper;
    public static final int REQUEST_SPECIAL_APP=2;
    public static final int RESULT_SPECIAL_APP=3;
    public static final String DB_APPINFO="info.db";
//    public static final String seId="451000000000000020160328000000010005";


    /**
     * 接收来自应用下载或者删除完成的广播消息，主要是为了更新变量appinstalling,appinstalled状态
     */
    private BroadcastReceiver bussinessUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            AppInformation appInformation = (AppInformation) bundle.getSerializable("BUSSINESS_UPDATE");

            //AppInformation appInformation = (AppInformation) intent.getSerializableExtra("BUSSINESS_UPDATE");
            appInformation.setAppinstalling(false);
            if(intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS)) {

                //String bussinessType = intent.getStringExtra("BUSSINESS_TYPE");
                String bussinessType = bundle.getString("BUSSINESS_TYPE");
                if (bussinessType.equals("download")) {
                    appInformation.setAppinstalled("yes");
                } else {
                    appInformation.setAppinstalled("no");
                }
                //修改全局变量map中的值
//                MyApplication.appInstalling.put(appInformation.getIndex(), appInformation.isAppinstalling());

            }
            else
            {
                //不需要更新appinstalled状态
            }
            //更新applist
            appList.set(appInformation.getIndexForlistview(),appInformation);
            //刷新Listview
            busAdapter.notifyDataSetChanged();
        }
    };

    private IntentFilter makeBussinessUpdateIntentFilter(){
        IntentFilter bussinessUpdateIntentFilter = new IntentFilter();
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS);
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED);
        return bussinessUpdateIntentFilter;
    }

    /**
     * 当图片下载完成后，更新变量appList,主要是更新localpicpath
     */
    public Handler updatePicHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                AppInformation appInfo = (AppInformation) msg.obj;
                for(int i=0;i<appList.size();i++){
                    if (appList.get(i).getIndex().equals(appInfo.getIndex())){
                        appList.set(i,appInfo);
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建数据库
        dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
//
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_appstore,container,false);
        //应用展示信息加载前的控件及展示
        final LinearLayout loading= (LinearLayout) view.findViewById(R.id.id_ll_loading);
        loading.setVisibility(View.VISIBLE);
        //ListView及item单击响应事件
        final ListView listViewAppStore = (ListView) view.findViewById(R.id.id_listview_bus);
        listViewAppStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInformation appInformation = appList.get(i);//appInformationList.get(i);
                appInformation.setIndexForlistview(i);
                Intent intent = new Intent(view.getContext(),SpecialAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("APP_PARAMETER", (Serializable) appInformation);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_SPECIAL_APP);
            }
        });
        //当获取到appList全部数据后，在ListView中展示
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    //创建adapter，绑定listview的itemview的内容
                    busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore, appList,listViewAppStore,updatePicHandler);
                }
                listViewAppStore.setAdapter(busAdapter);
                loading.setVisibility(View.INVISIBLE);
            }
        };
        //获取AppInformation List
        if(appList!=null){
//            handler.sendEmptyMessage(0);
            /////////同一个线程内，尽量不要用消息的方式，效率低////////////////
            busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore, appList,listViewAppStore,updatePicHandler);
            listViewAppStore.setAdapter(busAdapter);
            loading.setVisibility(View.INVISIBLE);
        }
        else {
            LogUtil.debug("appList is not null");
            //appList标识为静态变量static以后，切换Fragment，Fragment执行OnDestroy，appList不释放，不需要从数据库获取
            //从数据库获取appInformation
            if (MyApplication.dataFromNet) {
                appList = new ArrayList<AppInformation>();
                SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
                Cursor cursor = dbRead.query("appinfo", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String appindex = cursor.getString(cursor.
                                getColumnIndex("appindex"));
                        String picurl = cursor.getString(cursor.
                                getColumnIndex("picurl"));
                        String appname = cursor.getString(cursor.
                                getColumnIndex("appname"));
                        String appsize = cursor.getString(cursor.
                                getColumnIndex("appsize"));
                        String apptype = cursor.getString(cursor.
                                getColumnIndex("apptype"));
                        String spname = cursor.getString(cursor.
                                getColumnIndex("spname"));
                        String appdesc = cursor.getString(cursor.
                                getColumnIndex("appdesc"));
                        String appinstalled = cursor.getString(cursor.
                                getColumnIndex("appinstalled"));
                        String appid = cursor.getString(cursor.
                                getColumnIndex("appid"));
                        String localpicpath = cursor.getString(cursor.
                                getColumnIndex("localpicpath"));
                        AppInformation appInformation = new AppInformation(appindex,picurl,appname,appsize,apptype,
                                spname,appdesc,appinstalled,appid,false,-1,localpicpath);
                        appList.add(appInformation);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                //获取全局变量map中的值给appList
                for (Map.Entry<String, Boolean> entry : MyApplication.appInstalling.entrySet()) {
                    String index = entry.getKey();
                    Boolean installing = entry.getValue();
                    for (int i = 0; i < appList.size(); i++) {
                        AppInformation appInfo = appList.get(i);
                        if (appInfo.getIndex().equals(index)) {
                            appInfo.setAppinstalling(installing);
                        }
                    }
                }
//                handler.sendEmptyMessage(0);
                busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore,
                        appList,listViewAppStore,updatePicHandler);
                listViewAppStore.setAdapter(busAdapter);
                loading.setVisibility(View.INVISIBLE);
            }
            else {
                //从网络获取appInformation
                final String requestType = "dbquery";
                final String requestData = "applistquery";
                if(MyApplication.seId.equals("")){
                    MyApplication app = (MyApplication) MyApplication.getContextObject();
                    String bleDevAddr = app.getBluetoothDevAddr();
                    if(bleDevAddr.isEmpty())
                        return view;
                    BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
                            bleDevAddr);
                    bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                        @Override
                        public void onBLEPrepared() {
                            ApplyPersonalizationService.getAppinfoFromWebservice(MyApplication.seId, requestType, requestData,
                                    new TSMAppInformationCallback() {
                                        @Override
                                        public void getAppInfo(String xml) {
                                            if(xml.isEmpty()){
                                                //没有获取到Applet相关信息,说明网络存在问题，或者token失效，进入登录界面
                                                // isAdded(),Return true if the fragment is currently added to its activity.
                                                // 因为网络是异步的，为了确保view不为Null，先判断fragment is attached to activity，or not
//                            if(isAdded()) {
//                                Intent intent = new Intent(view.getContext(), DigtalpwdLoginActivity.class);
//                                startActivity(intent);
//
//                            }
                                                return;
                                            }
                                            //解析xml
                                            TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                                            //获取List<AppInformation>
                                            LogUtil.debug("get applist");
                                            appList = entity.getAppInformationList();
                                            //appInfoPrepared=true;
                                            //获取全局变量map中的值给appList
                                            for (Map.Entry<String, Boolean> entry : MyApplication.appInstalling.entrySet()) {
                                                String index = entry.getKey();
                                                Boolean installing = entry.getValue();
                                                for (int i = 0; i < appList.size(); i++) {
                                                    AppInformation appInfo = appList.get(i);
                                                    if (appInfo.getIndex().equals(index)) {
                                                        appInfo.setAppinstalling(installing);
                                                    }
                                                }
                                            }
                                            //从网络获取数据，必须使用消息的方式，因为网络获取数据是异步的
                                            handler.sendEmptyMessage(0);
//                        busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore, appList,listViewAppStore,updatePicHandler);
                                            //将数据写入数据库
                                            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                                            new DatabaseHandler().insertDB(dbWrite, appList);
                                            MyApplication.dataFromNet = true;
                                        }
                                    });
                        }
                    });
                }

            }
        }
//        listViewAppStore.setAdapter(busAdapter);
//        loading.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册receiver
        getActivity().registerReceiver(bussinessUpdateReceiver,makeBussinessUpdateIntentFilter());
    }

    /**
     * 关闭SpecialAppActivity的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_SPECIAL_APP && resultCode==RESULT_SPECIAL_APP){
            AppInformation appInformation = (AppInformation) data.getSerializableExtra("APP_INFO");
//            appInformationList.set(appInformation.getIndexForlistview(),appInformation);
            appList.set(appInformation.getIndexForlistview(),appInformation);
            busAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.debug("AppStoreFragment onPause");
        if(appList==null)
            LogUtil.debug("appList is null");
        //

    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.debug("AppStoreFragment onStop");
        if(appList==null)
            LogUtil.debug("appList is null");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.debug("AppStoreFragment onDestroy");
        if(appList==null)
            LogUtil.debug("appList is null");
        //取消receiver注册
        getActivity().unregisterReceiver(bussinessUpdateReceiver);
    }
}
