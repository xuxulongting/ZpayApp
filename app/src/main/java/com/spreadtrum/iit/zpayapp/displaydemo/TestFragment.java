package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.database.DatabaseHandler;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.webservice.ApplyPersonalizationService;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by SPREADTRUM\ting.long on 16-9-21.
 */
public class TestFragment extends Fragment {
    private ListView listView;
    private CursorAdapter cursorAdapter;
    AppDisplayDatabaseHelper dbHelper;
    private Button btnOperaCard;
    private LinearLayout linearLayoutBar;
    private List<AppInformation> appList = new ArrayList<>();

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
            cursorAdapter.notifyDataSetChanged();
        }
    };

    private IntentFilter makeBussinessUpdateIntentFilter(){
        IntentFilter bussinessUpdateIntentFilter = new IntentFilter();
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS);
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED);
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
        View view = inflater.inflate(R.layout.fragment_appstore,container,false);
        final LinearLayout loading= (LinearLayout) view.findViewById(R.id.id_ll_loading);
        listView = (ListView) view.findViewById(R.id.id_listview_bus);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInformation appInformation = appList.get(i);//appInformationList.get(i);
                appInformation.setIndexForlistview(i);
                Intent intent = new Intent(view.getContext(),SpecialAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("APP_PARAMETER", appInformation);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_SPECIAL_APP);
            }
        });
        loading.setVisibility(View.VISIBLE);
        final SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
        Handler appInfoHandler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==0) {
                    Cursor cursor = dbWrite.query(AppDisplayDatabaseHelper.TABLE_APPINFO, null, null, null, null, null, null);
                    cursorAdapter = new AppStoreCusorAdapter(getActivity(), cursor, true);
                    listView.setAdapter(cursorAdapter);
                    loading.setVisibility(View.INVISIBLE);
                }
            }
        };
        //从网络获取数据
        getAppInformationFromTSM(appInfoHandler);
        return view;
    }

    private void getAppInformationFromTSM(final Handler handler){
        if(MyApplication.dataFromNet){
            handler.sendEmptyMessage(0);
        }
        //从网络获取appInformation
        String requestType = "dbquery";
        String requestData = "applistquery";
        ApplyPersonalizationService.getAppinfoFromWebservice(MyApplication.seId, requestType, requestData, new TSMAppInformationCallback() {
            @Override
            public void getAppInfo(String xml) {
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

                //将数据写入数据库
                SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                new DatabaseHandler().insertDB(dbWrite, appList);
                MyApplication.dataFromNet = true;
                //
                handler.sendEmptyMessage(0);
            }
        });
    }

    public static final int REQUEST_SPECIAL_APP=2;
    public static final int RESULT_SPECIAL_APP=3;

}
