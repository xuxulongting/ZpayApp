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
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.tcp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPSocket;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.BitmapCache;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMPersonalizationWebservice;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static java.lang.Thread.currentThread;

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
                //获取蓝牙句柄
                //if(bluetoothControl==null){
                final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                if (bluetoothControl==null)
                {
                    //修改listview中button视图，修改item的值，就相当于修改了appList变量
                    item.setAppinstalling(false);
                    //修改全局变量map中的值
                    MyApplication.appInstalling.put(item.getIndex(),true);
                    //刷新Listview
                    notifyDataSetChanged();
                }
                bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                    @Override
                    public void onBLEPrepared() {
                        //BLE已连接上，且SE通道已打开
                        //获取task id
                        RequestTaskidEntity entity = MessageBuilder.getRequestTaskidEntity(item,
                                BussinessTransaction.TASK_TYPE_DOWNLOAD);
                        TSMPersonalizationWebservice.getTSMTaskid(MyApplication.seId, "dbinsert", entity,
                                new TSMAppInformationCallback() {
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

                                        //（1)使用RxJava实现从当前线程跳转到主线程
                                        Observable.just(MyApplication.DOWNLOAD_SUCCESS)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<Integer>() {
                                                    @Override
                                                    public void call(Integer integer) {
                                                        Toast.makeText(MyApplication.getContextObject(),"帮卡成功",Toast.LENGTH_LONG);
                                                    }
                                                });
                                         //（2)使用消息方式发送消息到主线程进行操作
//                                        MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_SUCCESS);
                                    }

                                    @Override
                                    public void onTaskExecutedFailed() {
                                        AppInformation appInformation = item;
                                        LogUtil.debug("internal item index:"+appInformation.getIndex());
                                        transaction.broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"download");
                                        MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_FAILED);
                                    }

                                    @Override
                                    public void onTaskNotExecuted() {
                                        AppInformation appInformation = item;
                                        transaction.broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED,appInformation,"notexecuted");
                                    }
                                });
                            }
                        });

                    }
                });
                //}
            }
        });

        //单击，则取消绑定,TSM不支持取消功能
//        linearLayoutBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //关闭tsm连接
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TCPSocket tcpSocket =  TCPSocket.getInstance(NetParameter.IPAddress, NetParameter.Port);
//                        tcpSocket.closeSocket();
//                    }
//                }).start();
////                //关闭蓝牙连接
////                MyApplication app = (MyApplication) mContext.getApplicationContext();
////                BluetoothControl bluetoothControl = BluetoothControl.getInstance(mContext,
////                        app.getBluetoothDevAddr());
////                bluetoothControl.disconnectBluetooth();
//            }
//        });
    }
}
