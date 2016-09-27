package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.network.webservice.ApplyPersonalizationService;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-26.
 */
public class AppStoreCommonAdapter extends CommonAdapter<AppInformation> {
    private List<AppInformation> appList=null;
    private Context mContext=null;
    private Button btnOperaCard;
    private LinearLayout linearLayoutBar;
    private ListView listViewAppStore;
    /**
     * 当图片下载完成后，更新变量appList,主要是更新localpicpath
     */
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

//    public AppStoreCommonAdapter(Context context, int layoutId, List datas) {
//        super(context, layoutId, datas);
//        appList = datas;
//        mContext = context;
//    }

    public AppStoreCommonAdapter(Context context,int layoutId,List datas,View parent){
        super(context, layoutId, datas);
        appList = datas;
        mContext = context;
        listViewAppStore = (ListView) parent;
    }

    @Override
    protected void convert(ViewHolder viewHolder, final AppInformation item, final int position) {
        //显示应用名称
        viewHolder.setText(R.id.id_tv_appname,item.getAppname());
        //显示图片
        ImageView imageView = viewHolder.getView(R.id.id_iv_appicon);
        imageView.setImageResource(R.drawable.refresh);//空白图片记得加上，否则，在图片下载成功之前，会显示重复利用的itemview的图片
        String url = item.getPicurl();
        imageView.setTag(url);
//                            LogUtil.debug("setTag:"+url);
        if(item.getLocalpicpath()==null){
            if(item.isPicdownloading()==false) {
                //网络下载图片保存并显示

//                                    DownloadImage(url, imageView, item, listViewAppStore);
                new ImageLoaderUtil(updatePicHandler).DownloadImage(url,item,listViewAppStore);
                item.setPicdownloading(true);//主要是为了防止图片重复下载，另外，增加这个约束以后，图片也不会出现乱序的问题了（现在还不知道原因）
            }

        }
        else{
            //查找本地图片
            Bitmap bitmap = ImageLoaderUtil.getLoacalBitmap(item.getLocalpicpath());
            if(bitmap==null){
                //本地图片缓存被清空
                new ImageLoaderUtil(updatePicHandler).DownloadImage(url,item,listViewAppStore);
            }
            else
                viewHolder.setImageBitmap(R.id.id_iv_appicon,bitmap);
        }

        btnOperaCard = viewHolder.getView(R.id.id_btn_appopra);
//                            progressBar = viewHolder.getView(R.id.id_pb_appProgressBar);
        linearLayoutBar  =viewHolder.getView(R.id.id_ll_downloading);
        //LogUtil.debug("isAppinstalling is:"+item.isAppinstalling());
        if(item.isAppinstalling(item.getIndex())==true){
            btnOperaCard.setVisibility(View.INVISIBLE);
//                                progressBar.setVisibility(View.VISIBLE);
            linearLayoutBar.setVisibility(View.VISIBLE);
        }
        else
        {
            if(item.getAppinstalled().equals("yes")){
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
        btnOperaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //连接BLE
                MyApplication app = (MyApplication) mContext.getApplicationContext();//(MyApplication) getActivity().getApplication();
                final String bluetoothDevAddr = app.getBluetoothDevAddr();
                if(bluetoothDevAddr.isEmpty()){
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("没有选择蓝牙设备，请到“设置”页面选择")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                    return;
                }
                //修改listview中button视图，修改item的值，就相当于修改了appList变量
                item.setAppinstalling(true);
                //修改全局变量map中的值
                MyApplication.appInstalling.put(item.getIndex(),true);
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
//                        //获取taskid
//                        RequestTaskidEntity entity=new RequestTaskidEntity();
//                        String appid = item.getAppid();
//                        byte[] bAppid = new byte[5];
//                        byte[] data = ByteUtil.StringToByteArray(appid);
//                        System.arraycopy(data,0,bAppid,5-data.length,data.length);
//
//                        entity.setTasktype(AppStoreFragment.TASK_TYPE_DOWNLOAD);
//                        String strCmd = AppStoreFragment.TASK_TYPE_DOWNLOAD+"05"+ByteUtil.bytesToString(bAppid,5);
//                        entity.setTaskcommand(strCmd);
                        RequestTaskidEntity entity = MessageBuilder.getRequestTaskidEntity(item,BussinessTransaction.TASK_TYPE_DOWNLOAD);
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
                                item.setIndexForlistview(position);//标识在listview中的位置
                                //下载应用
//                                new BussinessTransaction().DownloadApplet(bluetoothControl,bTaskId,item);
                                final BussinessTransaction transaction = new BussinessTransaction();
                                LogUtil.debug("item index:"+item.getIndex());
                                transaction.DownloadApplet(bluetoothControl, bTaskId, item, new TsmTaskCompleteCallback() {
//                                    AppInformation appInformation = item;
                                    @Override
                                    public void onTaskExecutedSuccess() {
                                        AppInformation appInformation = item;
                                        LogUtil.debug("internal item index:"+appInformation.getIndex());
                                        transaction.broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS,
                                                appInformation,"download");

                                        MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_SUCCESS);
                                    }

                                    @Override
                                    public void onTaskExecutedFailed() {
                                        AppInformation appInformation = item;
                                        LogUtil.debug("internal item index:"+appInformation.getIndex());
                                        transaction.broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"download");
                                        MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_FAILED);
                                    }
                                });
                                //DeleteApplet(bTaskId,handler);
                            }
                        });

                    }
                });
                //}
            }
        });
    }
}
