package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.webservice.ApplyPersonalizationService;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-22.
 */
public class AppStoreCusorAdapter extends CursorAdapter{
    private List<AppInformation> appList= new ArrayList<>();
    private Context mContext;
    private ListView mListView=null;

    //更新appList
    private Handler updatePicHandler = new Handler(){
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
    public AppStoreCusorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    public AppStoreCusorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
//        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_appstore, viewGroup,
//                false);
        if(mListView==null)
            mListView = (ListView) viewGroup;
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View itemView=inflater.inflate(R.layout.list_item_appstore ,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.btnOperaCard = (Button) itemView.findViewById(R.id.id_btn_appopra);
        viewHolder.linearLayoutBar = (LinearLayout) itemView.findViewById(R.id.id_ll_downloading);
        viewHolder.textViewAppname = (TextView) itemView.findViewById(R.id.id_tv_appname);
        viewHolder.imageViewAppicon = (ImageView) itemView.findViewById(R.id.id_iv_appicon);
        itemView.setTag(viewHolder);
//        LogUtil.debug("setTag ViewHolder");
        LogUtil.debug("itemview:"+itemView.toString());
        return itemView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        AppInformation appInformation = null;
        final String appindex = cursor.getString(cursor.
                getColumnIndex("appindex"));

        int i=0;
        for(i=0;i<appList.size();i++){
            if(appList.get(i).getIndex().equals(appindex)){
                appInformation = appList.get(i);
                break;
            }
        }

        if(i==appList.size()) {
            String appname = cursor.getString(cursor.
                    getColumnIndex("appname"));
            String picurl = cursor.getString(cursor.
                    getColumnIndex("picurl"));
            String localpicpath = cursor.getString(cursor.
                    getColumnIndex("localpicpath"));
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

            appInformation = new AppInformation(appindex, picurl, appname, appsize, apptype,
                    spname, appdesc, appinstalled, appid, false, -1, localpicpath);
        }
        LogUtil.debug("bindView");


        //显示应用名称
        viewHolder.textViewAppname.setText(appInformation.getAppname());
        //显示图片
        ImageView imageView = viewHolder.imageViewAppicon;
//        ImageView imageView = (ImageView) view.findViewById(R.id.id_iv_appicon);
        //imageView.setTag(picurl);
        viewHolder.imageViewAppicon.setTag(appInformation.getPicurl());
//        LogUtil.debug("setTag:"+appInformation.getPicurl()+"; "+ imageView.toString());
        ImageLoaderUtil imageLoader = new ImageLoaderUtil(updatePicHandler);
        if(appInformation.getLocalpicpath()==null){
            if(appInformation.isPicdownloading()==false) {
                //网络下载图片保存并显示
                imageLoader.DownloadImage(appInformation.getPicurl(), imageView, appInformation, mListView);
                //appInformation.setPicdownloading(true);

            }

        }
        else{
            //查找本地图片
            Bitmap bitmap = imageLoader.getLoacalBitmap(appInformation.getLocalpicpath());
            if(bitmap==null){
                //本地图片缓存被清空
                imageLoader.DownloadImage(appInformation.getPicurl(),imageView,appInformation, mListView);
            }
            else {
                LogUtil.debug("get local bitmap");
                imageView.setImageBitmap(bitmap);
            }
        }
        if(i==appList.size()){
            appList.add(appInformation);
        }
        else
        {
            appList.set(i,appInformation);
        }


        Button btnOperaCard = viewHolder.btnOperaCard;
        LinearLayout linearLayoutBar = viewHolder.linearLayoutBar;
        if(appInformation.isAppinstalling(appindex)==true){
            btnOperaCard.setVisibility(View.INVISIBLE);
            linearLayoutBar.setVisibility(View.VISIBLE);
        }
        else
        {
            if(appInformation.getAppinstalled().equals("yes")){
                btnOperaCard.setText("已绑卡");
                btnOperaCard.setEnabled(false);
            }
            else
            {
                btnOperaCard.setText("绑卡");
                btnOperaCard.setEnabled(true);
            }
            btnOperaCard.setVisibility(View.VISIBLE);
//                                progressBar.setVisibility(View.INVISIBLE);
            linearLayoutBar.setVisibility(View.INVISIBLE);
        }
        final AppInformation finalAppInformation = appInformation;
        btnOperaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //连接BLE
//                MyApplication app =MyApplication.getInstance();
                //(MyApplication) getActivity().getApplication();
                MyApplication app = (MyApplication) mContext.getApplicationContext();
                final String bluetoothDevAddr = app.getBluetoothDevAddr();
                if(bluetoothDevAddr.isEmpty()){
                    new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("没有选择蓝牙设备，请到“设置”页面选择")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                    return;
                }
//                //修改listview中button视图，修改item的值，就相当于修改了appList变量
//                item.setAppinstalling(true);
                //修改全局变量map中的值
                MyApplication.appInstalling.put(cursor.getString(cursor.
                        getColumnIndex("appindex")),true);
                //刷新Listview
                notifyDataSetChanged();
                //获取蓝牙读写句柄
                //if(bluetoothControl==null){
                final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                    @Override
                    public void onBLEPrepared() {
                        //BLE已连接上，且SE通道已打开
                        LogUtil.debug("onBLEPrepared");
                        //获取taskid
                        RequestTaskidEntity entity=new RequestTaskidEntity();
                        String appid = cursor.getString(cursor.getColumnIndex("appid"));//item.getAppid();
                        byte[] bAppid = new byte[5];
                        byte[] data = ByteUtil.StringToByteArray(appid);
                        System.arraycopy(data,0,bAppid,5-data.length,data.length);
                        //下载应用
                        entity.setTasktype(TASK_TYPE_DOWNLOAD);
                        String strCmd = TASK_TYPE_DOWNLOAD+"05"+ByteUtil.bytesToString(bAppid,5);
                        entity.setTaskcommand(strCmd);
                        //同步应用
//                                    entity.setTasktype(TASK_TYPE_SYNC);
//                                    entity.setTaskcommand("DA0103");
                        ApplyPersonalizationService.getTSMTaskid(MyApplication.seId, "dbinsert", entity, new TSMAppInformationCallback() {
                            @Override
                            public void getAppInfo(String xml) {
                                //解析xml
                                TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                                String taskId = entity.getTaskId();
                                int dectask = ByteUtil.parseInt(taskId,10,0);
                                byte[] data = ByteUtil.int2Bytes(dectask);
                                byte[] bTaskId = new byte[20];
                                System.arraycopy(data,0,bTaskId,20-data.length,data.length);
//                                item.setIndexForlistview(position);//标识在listview中的位置
                                new BussinessTransaction().DownloadApplet(bluetoothControl,bTaskId, finalAppInformation);
                                //DeleteApplet(bTaskId,handler);
                            }
                        });

                    }
                });
                //}
            }
        });
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    private class ViewHolder{
        TextView textViewAppname;
        ImageView imageViewAppicon;
        Button btnOperaCard;
        LinearLayout linearLayoutBar;

    }

    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
}