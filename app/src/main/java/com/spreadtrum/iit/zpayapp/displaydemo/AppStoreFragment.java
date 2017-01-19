package com.spreadtrum.iit.zpayapp.displaydemo;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.common.AppGlobal;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.PullToRefreshLayoutTellH.PullToRefreshLayout;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.database.DatabaseHandler;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.network.heartbeat.HeartBeatThread;
import com.spreadtrum.iit.zpayapp.utils.NetworkUtils;
import com.spreadtrum.iit.zpayapp.bussiness.ResultCallback;
import com.spreadtrum.iit.zpayapp.bussiness.ZAppStoreApi;
import com.spreadtrum.iit.zpayapp.register_login.DigtalpwdLoginActivity;
import com.zhy.adapter.abslistview.CommonAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class AppStoreFragment extends Fragment {
    private CommonAdapter busAdapter= null;
    private static List<AppInformation> appList=null;
    private ListView listViewAppStore;
    private Button btnOperaCard;
    private LinearLayout linearLayoutBar;
    private AppDisplayDatabaseHelper dbHelper;
    public static final int REQUEST_SPECIAL_APP=2;
    public static final int RESULT_SPECIAL_APP=3;
    public static final String DB_APPINFO="info.db";

    //PullToRefreshLayout
    private PullToRefreshLayout mRefreshLayout=null;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;
    private ImageView imgDone;

    /**
     * 接收来自应用下载或者删除完成的广播消息，主要是为了更新变量appinstalling,appinstalled状态
     */
    private BroadcastReceiver bussinessUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.debug("onReceive:"+intent.getAction());
            if(intent.getAction().equals(HeartBeatThread.ACTION_BUSSINESS_REMOTE_MANAGEMENT)){
                LogUtil.debug("HEARTBEAT","receive :"+intent.getAction());
                getListDataFromTSM(context);
            }
            else {
                Bundle bundle = intent.getExtras();
                AppInformation appInformation = (AppInformation) bundle.getSerializable("BUSSINESS_UPDATE");

                //AppInformation appInformation = (AppInformation) intent.getSerializableExtra("BUSSINESS_UPDATE");
                appInformation.setAppinstalling(false);

                if (intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS)) {
                    AppGlobal.isOperated = false;
                    String bussinessType = bundle.getString("BUSSINESS_TYPE");
                    if (bussinessType.equals("download")) {
                        appInformation.setAppinstalled("yes");
                    } else {
                        appInformation.setAppinstalled("no");
                    }
                    //更新applist
                    appList.set(appInformation.getIndexForlistview(), appInformation);
                    //刷新Listview
                    busAdapter.notifyDataSetChanged();
                } else if (intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED)){
                    //不需要更新appinstalled状态
                    AppGlobal.isOperated = false;
                    //从后台获取列表
                    getListDataFromTSM(MyApplication.getContextObject());
                }
                else {
                    AppGlobal.isOperated = false;
//                    //更新applist
//                    appList.set(appInformation.getIndexForlistview(), appInformation);
                    //刷新Listview
                    busAdapter.notifyDataSetChanged();
                }
//                //更新applist
//                appList.set(appInformation.getIndexForlistview(), appInformation);
//                //刷新Listview
//                busAdapter.notifyDataSetChanged();
            }
        }
    };

    private IntentFilter makeBussinessUpdateIntentFilter(){
        IntentFilter bussinessUpdateIntentFilter = new IntentFilter();
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS);
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED);
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED);
        bussinessUpdateIntentFilter.addAction(HeartBeatThread.ACTION_BUSSINESS_REMOTE_MANAGEMENT);
        return bussinessUpdateIntentFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建数据库
        dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_appstore,container,false);
        //ListView及item单击响应事件
        listViewAppStore = (ListView) view.findViewById(R.id.id_listview_bus);
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
                    busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore, appList);//,listViewAppStore,updatePicHandler);
                }
                listViewAppStore.setAdapter(busAdapter);
            }
        };
        //获取AppInformation List
        if(appList!=null){
            /////////同一个线程内，尽量不要用消息的方式，效率低////////////////
            busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore,
                    appList);//,listViewAppStore,updatePicHandler);
            listViewAppStore.setAdapter(busAdapter);
        }
        else {
            LogUtil.debug("appList is null");
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
                        String applocked = cursor.getString(cursor.
                                getColumnIndex("applocked"));
                        String appid = cursor.getString(cursor.
                                getColumnIndex("appid"));
                        String localpicpath = cursor.getString(cursor.
                                getColumnIndex("localpicpath"));
                        AppInformation appInformation = new AppInformation(appindex,picurl,appname,appsize,apptype,
                                spname,appdesc,appinstalled,appid,false,-1,localpicpath,applocked);
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
                busAdapter = new AppStoreCommonAdapter(view.getContext(), R.layout.list_item_appstore,
                        appList);//,listViewAppStore,updatePicHandler);
                listViewAppStore.setAdapter(busAdapter);
            }
        }
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        fragmentInterface.setFragment(this);
    }

    @Override
    public void onResume() {
        if(appList!=null){
            /////////同一个线程内，尽量不要用消息的方式，效率低////////////////
            busAdapter = new AppStoreCommonAdapter(MyApplication.getContextObject(), R.layout.list_item_appstore,
                    appList);//,listViewAppStore,updatePicHandler);
            listViewAppStore.setAdapter(busAdapter);
            super.onResume();
        }
        else {
            if (mRefreshLayout != null) {
                //FIXME
                mRefreshLayout.setRefreshing(true);
            }
            super.onResume();
        }
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

    public void getListDataFromTSM(final Context context){
        MyApplication app = (MyApplication) MyApplication.getContextObject();
        String bleDevAddr="";
        //检查蓝牙设备地址
        bleDevAddr = AppGlobal.bluetoothDevAddr;//app.getBluetoothDevAddr();
        if (bleDevAddr.isEmpty()) {
            Toast.makeText(MyApplication.getContextObject(), "请选择蓝牙设备", Toast.LENGTH_LONG).show();
            //停止刷新，否则下次mRefreshLayout.setRefreshing(true);不再起作用
            mRefreshLayout.setRefreshing(false);
//            Intent intent = new Intent(getActivity(), BluetoothSettingsActivity.class);
//            startActivity(intent);
            return;
        }
        ZAppStoreApi.getListDataFromTSM(bleDevAddr, new ResultCallback<List<AppInformation>>() {
            @Override
            public void onPreStart() {

            }

            @Override
            public void onSuccess(List<AppInformation> response) {
                appList = response;
                Observable.just(0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                MyApplication.dataFromNet = true;
                                mRefreshLayout.setRefreshing(false);
                                busAdapter = new AppStoreCommonAdapter(context, R.layout.list_item_appstore,
                                        appList);//,listViewAppStore,updatePicHandler);
                                if (listViewAppStore!=null)
                                    listViewAppStore.setAdapter(busAdapter);
                            }
                        });
                //将数据写入数据库
                if (dbHelper==null){
                    //创建数据库
                    dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
                }
                SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                new DatabaseHandler().insertDB(dbWrite, appList);
            }

            @Override
            public void onFailed(String error) {
                //没有获取到Applet相关信息,说明网络存在问题，或者token失效，进入登录界面
                // isAdded(),Return true if the fragment is currently added to its activity.
                // 因为网络是异步的，为了确保view不为Null，先判断fragment is attached to activity，or not
                //停止刷新
                mRefreshLayout.setRefreshing(false);
                if (error.equals("808")) {
                    if (isAdded()) {
                        Intent intent = new Intent(getActivity(), DigtalpwdLoginActivity.class);
                        startActivity(intent);
                    }
                }
                else if (error.equals("bluetooth connection error")){
                    Toast.makeText(MyApplication.getContextObject(),"蓝牙连接出现问题",Toast.LENGTH_LONG).show();
                }
                else{
                    //检查网络状态
                    if (!NetworkUtils.isNetworkConnected(MyApplication.getContextObject()))
                        Toast.makeText(MyApplication.getContextObject(),"网络未连接",Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MyApplication.getContextObject(),"网络出现异常",Toast.LENGTH_LONG).show();

                }


            }
        });
    }

    public void initView(final View view) {
        mRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_widget);
        createRefreshView();
        mRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListenerAdapter() {
            @Override
            public void onRefresh() {
//                LogUtil.debug("onRefresh:"+String.valueOf(Thread.currentThread().getId()));
                textView.setText("正在刷新...");
                imgDone.setVisibility(View.GONE);
                imageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                getListDataFromTSM(view.getContext());
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onDragDistanceChange(float distance, float percent, float offset) {
//                Log.d("TAG","distance:"+String.valueOf(distance)+",percent:"+String.valueOf(percent)+",offset:"+
//                        String.valueOf(percent));
                if (percent >= 1.0f) {
                    //只有手动刷新，才会进入
                    textView.setText("松开刷新");
                    imgDone.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setRotation(180);
                } else {
                    textView.setText("下拉刷新");
                    imgDone.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setRotation(0);
                }
            }

            @Override
            public void onFinish() {
                textView.setText("刷新成功");
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                imgDone.setVisibility(View.VISIBLE);
            }
        });
        mRefreshLayout.setFinishRefreshToPauseDuration(800);
        if(!MyApplication.dataFromNet)
            mRefreshLayout.setRefreshing(true);
    }

    private View createRefreshView() {
        View headerView=mRefreshLayout.setRefreshView(R.layout.layout_refresh_view);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        textView.setText("下拉刷新");
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        imgDone=(ImageView)headerView.findViewById(R.id.img_done);
        imgDone.setImageResource(R.drawable.ic_check_circle_black);
        imgDone.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }
}
