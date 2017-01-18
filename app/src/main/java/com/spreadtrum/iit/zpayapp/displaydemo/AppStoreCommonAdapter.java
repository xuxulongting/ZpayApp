package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.spreadtrum.iit.zpayapp.common.AppGlobal;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.bussiness.TsmTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPNetParameter;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPSocket;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.BitmapCache;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by SPREADTRUM\ting.long on 16-9-26.
 */
public class AppStoreCommonAdapter extends CommonAdapter<AppInformation> {
    private List<AppInformation> appList=null;
    private Context mContext=null;
    private Button btnOperaCard;
    private LinearLayout linearLayoutBar;
    private ListView listViewAppStore;
    private Handler updatePicHandler=null;
    /** AppStoreFragment的Adapter构造函数
    * @param context   上下文，必须是view上下文，而不是application上下文
    * @param layoutId  item view的资源Id
    * @param datas item view要绑定的数据
    */
    public AppStoreCommonAdapter(Context context,int layoutId,List datas){
        super(context, layoutId, datas);
        appList = datas;
        mContext = context;
    }

    /**
     * AppStoreFragment的Adapter构造函数
     * @param context   上下文，必须是view上下文，而不是application上下文
     * @param layoutId  item view的资源Id
     * @param datas item view要绑定的数据
     * @param parent    这里指ListView
     * @param updateHandler 下载图片后，以消息的方式更新appList的Handler，这样，就不必从数据库读取
     */
    public AppStoreCommonAdapter(Context context,int layoutId,List datas,View parent,Handler updateHandler){
//        super(context, layoutId, datas);
        this(context,layoutId,datas);
        appList = datas;
        mContext = context;
        listViewAppStore = (ListView) parent;
        this.updatePicHandler = updateHandler;
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
        //使用volley下载图片，并使用LruCache进行Memmory缓存，并自带Disk缓存
        RequestQueue requestQueue = RequestQueueUtils.getInstance().getRequestQueue();
        ImageLoader imageLoader = new ImageLoader(requestQueue,new BitmapCache());
        ImageLoader.ImageListener imageListener = imageLoader.getImageListener(imageView,R.drawable.refresh,R.drawable.refresh);
        int maxImageViewWidth = imageView.getMaxWidth();//获取imageview最大宽度和高度
        int maxImageViewHeight = imageView.getMaxHeight();
        imageLoader.get(url,imageListener,maxImageViewWidth,maxImageViewHeight);//通过ImageView的最大宽度和高度对图片进行压缩

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
            //applet已锁定
            if (item.getApplocked().equals("yes")){
                btnOperaCard.setText("已锁定");
                btnOperaCard.setEnabled(false);
            }
            //applet已下载
            else if(item.getAppinstalled().equals("yes")){
                btnOperaCard.setText("已绑卡");
                btnOperaCard.setEnabled(false);
            }
            //待个人化
            else if (item.getAppinstalled().equals("not_personalized")){
                btnOperaCard.setText("继续下载");
                btnOperaCard.setEnabled(true);
            }
            else
            {
                btnOperaCard.setText("绑卡");
                btnOperaCard.setEnabled(true);
            }
            btnOperaCard.setVisibility(View.VISIBLE);
            linearLayoutBar.setVisibility(View.INVISIBLE);
        }
        item.setIndexForlistview(position);
        //下载Applet
        btnOperaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择BLE设备地址
//                final MyApplication app = (MyApplication) mContext.getApplicationContext();//(MyApplication) getActivity().getApplication();
//                final String bluetoothDevAddr = app.getBluetoothDevAddr();
                final String bluetoothDevAddr = AppGlobal.bluetoothDevAddr;
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
                //开始任务
                String taskType=BussinessTransaction.TASK_TYPE_DOWNLOAD;
                if (item.getAppinstalled().equals("not_personalized")){
                    taskType=BussinessTransaction.TASK_TYPE_PERSONALIZE;
                }
                final BussinessTransaction transaction = new BussinessTransaction();
                transaction.transactBussiness(item, taskType, new TsmTaskCompleteCallback() {
                    @Override
                    public void onTaskExecutedSuccess() {

                        AppInformation appInformation = item;
                        LogUtil.debug("internal item index:"+appInformation.getIndex());
                        new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS,
                                appInformation,"download");

                        //（1)使用RxJava实现从当前线程跳转到主线程
                        Observable.just(MyApplication.DOWNLOAD_SUCCESS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        Toast.makeText(MyApplication.getContextObject(),"绑卡成功",Toast.LENGTH_LONG).show();
                                    }
                                });
                        //（2)使用消息方式发送消息到主线程进行操作
//                       MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_SUCCESS);
                        //下载成功，断开蓝牙连接
                        AppGlobal.isOperated = false;
                    }

                    @Override
                    public void onTaskExecutedFailed() {
                        if (AppGlobal.isOperated==false)
                            return;

                        AppInformation appInformation = item;
                        LogUtil.debug("internal item index:"+appInformation.getIndex());
                        new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"download");
                        MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_FAILED);
                        //下载失败，断开蓝牙连接
//                        BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
//                                app.getBluetoothDevAddr());
//                        if (bluetoothControl!=null)
//                            bluetoothControl.disconnectBluetooth();
                        AppGlobal.isOperated = false;
                    }

                    @Override
                    public void onTaskNotExecuted() {

                        AppInformation appInformation = item;
                        new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED,appInformation,"notexecuted");
                        AppGlobal.isOperated = false;
                    }
                });
            }
        });
        //取消业务
        linearLayoutBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭tsm连接
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TCPSocket tcpSocket =  TCPSocket.getInstance(TCPNetParameter.IPAddress, TCPNetParameter.Port);
                        tcpSocket.closeSocket();
                        new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,
                                item,"download");
                        AppGlobal.isOperated = false;
                    }
                }).start();
//                //关闭蓝牙连接
//                MyApplication app = (MyApplication) mContext.getApplicationContext();
//                BluetoothControl bluetoothControl = BluetoothControl.getInstance(mContext,
//                        app.getBluetoothDevAddr());
//                bluetoothControl.disconnectBluetooth();


            }
        });
    }
}
