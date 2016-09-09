package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.display.Card;
//import com.zhy.adapter.recyclerview.CommonAdapter;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.tcp.TSMTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteListener;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class AppStoreFragment extends Fragment {
    private GridLayoutManager gridLayoutManagerBus;
    private GridLayoutManager gridLayoutManagerBank;
    private CommonAdapter busAdapter;
   // private CommonAdapter bankAdapter;
    private List<Card> listCardParameter = new ArrayList<Card>();
    private BluetoothControl bluetoothControl=null;
    private Button btnOperaCard;
    private ProgressBar progressBar;
    public static final int TSM_COMPLETE_SUCCESS=0;
    public static final int TSM_COMPLETE_FAILED=1;
    private Handler handler = new Handler(){
       public void handleMessage(Message msg){
            switch (msg.what){
                case AppStoreFragment.TSM_COMPLETE_SUCCESS:
                    btnOperaCard.setText("已绑定");
                    btnOperaCard.setEnabled(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    btnOperaCard.setVisibility(View.VISIBLE);
                    break;
                case TSM_COMPLETE_FAILED:
                    progressBar.setVisibility(View.INVISIBLE);
                    btnOperaCard.setVisibility(View.VISIBLE);
                    break;
            }
       }
   };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BluetoothSettingsActivity bluetoothSettingsActivity = new BluetoothSettingsActivity();
//        bluetoothSettingsActivity.setBluetoothDeviceListener(new BluetoothSettingsActivity.SelectBluetoothDeviceListener() {
//            @Override
//            public void onBluetoothDeviceSelected(String devAddr) {
//                if(bluetoothControl!=null) {
//                    LogUtil.debug(devAddr);
//                    bluetoothControl = new BluetoothControl(getActivity(), devAddr);
//                }
//            }
//        });
        //准备数据
        getCardListData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appstore,container,false);
        ListView listViewAppStore = (ListView) view.findViewById(R.id.id_listview_bus);
        listViewAppStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(view.getContext(),"CLICK",Toast.LENGTH_LONG).show();
                Card card = listCardParameter.get(i);
                Intent intent = new Intent(view.getContext(),SpecialAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("APP_PARAMETER", (Serializable) card);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        busAdapter = new CommonAdapter<Card>(view.getContext(), R.layout.list_item_appstore, listCardParameter) {
            @Override
            protected void convert(com.zhy.adapter.abslistview.ViewHolder viewHolder, final Card card, final int position) {
                viewHolder.setImageResource(R.id.id_iv_appicon,card.getCardView());
                viewHolder.setText(R.id.id_tv_appname,card.getCardName());
                //viewHolder.setText(R.id.id_tv_type,card.getCardType());
                btnOperaCard = viewHolder.getView(R.id.id_btn_appopra);
                progressBar = viewHolder.getView(R.id.id_pb_appProgressBar);
                btnOperaCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtil.debug("position:"+position);
                        LogUtil.debug(card.getCardName());
                        if(true) {
                            //btnOperaCard = viewHolder.getView(R.id.id_btn_appopra);
                            btnOperaCard.setVisibility(View.INVISIBLE);
                            //progressBar.setVisibility(View.VISIBLE);
                            return;
                        }
                        /////
                        MyApplication app = (MyApplication) getActivity().getApplication();
                        final String bluetoothDevAddr = app.getBluetoothDevAddr();
                        if(bluetoothDevAddr.isEmpty()){
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("提示")
                                    .setMessage("没有选择蓝牙设备，请到“设置”页面选择")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                            return;
                        }
                        if(bluetoothControl==null){
                            bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                        }
                        LogUtil.debug("no excuted");
                        btnOperaCard.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                            @Override
                            public void onBLEPrepared() {
                                //BLE准备好，开始发送数据
                                TCPTransferData tcpTransferData = new TCPTransferData();
                                //tcpTransferData.SyncApplet(bluetoothControl);
                                byte[] id = {0x2F, 0x7A};
                                byte[] taskId = new byte[20];
                                System.arraycopy(id,0,taskId,18,2);
                                tcpTransferData.SyncApplet(bluetoothControl, taskId);
                                //android 视图控件只能在主线程中去访问，用消息的方式
                                tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
                                    @Override
                                    public void onTaskExecutedSuccess() {
                                        handler.sendEmptyMessage(TSM_COMPLETE_SUCCESS);
                                    }

                                    @Override
                                    public void onTaskExecutedFailed(){
                                        handler.sendEmptyMessage(TSM_COMPLETE_FAILED);
                                    }
                                });
                            }
                        });

                    }
                });
            }
        };
        listViewAppStore.setAdapter(busAdapter);

        return view;
    }

    public void getCardListData() {
        Card busCard1 = new Card("旅行交通",R.drawable.bjgj,"北京公交一卡通");
        Card busCard2 = new Card("旅行交通",R.drawable.shgj,"上海公交一卡通");
        Card busCard3 = new Card("旅行交通",R.drawable.tjgj,"天津公交一卡通");
        listCardParameter.add(busCard1);
        listCardParameter.add(busCard2);
        listCardParameter.add(busCard3);
        Card bankCard1 = new Card("金融",R.drawable.china_bank,"中国银行");
        Card bankCard2 = new Card("金融",R.drawable.abc_bank,"中国农业银行");
        Card bankCard3 = new Card("金融",R.drawable.ccb_bank,"中国建设银行");
        Card bankCard4 = new Card("金融",R.drawable.icbc_bank,"中国工商银行");
        listCardParameter.add(bankCard1);
        listCardParameter.add(bankCard2);
        listCardParameter.add(bankCard3);
        listCardParameter.add(bankCard4);
    }




//    @Override
//    public void onBluetoothDeviceSelected(String devAddr) {
//        if(bluetoothControl==null) {
//            LogUtil.debug(devAddr);
//            bluetoothDevAddr = devAddr;
////            bluetoothControl = new BluetoothControl(getActivity().getApplicationContext(), devAddr);
//        }
//    }

}
