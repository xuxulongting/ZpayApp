package com.spreadtrum.iit.zpayapp.display;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-7-28.
 */
public class AllApplicationFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private com.zhy.adapter.recyclerview.CommonAdapter appAdapterAll;
    private CommonAdapter<AppParameter> appAdapterLocal;
    private int tabItem = -1;
    private Button btnDeleteApp;
    private List<AppParameter> appAllListData = new ArrayList<>();
    private List<AppParameter> appLocalListData = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            tabItem = bundle.getInt(ARGUMENT);
        }
        //初始化数据
        getItemDataFromSE();
        getItemDataFromTSM();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //使用了autolayout，记得使用三个参数的方法，且最后一个参数必须是false,
        final View frgview = inflater.inflate(R.layout.fragment_application,container,false);

        recyclerView = (RecyclerView) frgview.findViewById(R.id.id_recycler_view_app_all);
        //创建默认的grid LayoutManager
        gridLayoutManager = new GridLayoutManager(frgview.getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        if (tabItem==ApplicationActivity.TAB_LOCAL_APP){
            appAdapterLocal = new CommonAdapter<AppParameter>(frgview.getContext(), R.layout.item_app, appLocalListData) {
                @Override
                protected void convert(ViewHolder viewHolder, AppParameter item, int position) {
                    //viewHolder.setImageDrawable(R.id.id_iv_app_icon,item.getBitmapDrawable());
                    viewHolder.setText(R.id.id_tv_app_name,item.getAppType());
                }
            };
            recyclerView.setAdapter(appAdapterLocal);
            //已下载应用中的点击事件
            appAdapterLocal.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(frgview.getContext(),SpecialApplicationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ARGUMENT_APPTYPE,appLocalListData.get(position).getAppType());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });

        }
        else  {
            appAdapterAll = new CommonAdapter<AppParameter>(frgview.getContext(),R.layout.item_app, appAllListData) {

                @Override
                protected void convert(ViewHolder holder, AppParameter appParameter, int position) {
                    holder.setImageDrawable(R.id.id_iv_app_icon,appParameter.getBitmapDrawable());
                    holder.setText(R.id.id_tv_app_name,appParameter.getAppType());

                }
            };
            //全部应用中的点击事件
            appAdapterAll.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Toast.makeText(view.getContext(),appAllListData.get(position).getAppType(),Toast.LENGTH_LONG).show();
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
            recyclerView.setAdapter(appAdapterAll);

        }


        return frgview;
    }

    public List<AppParameter> getItemDataFromTSM(){
        AppParameter app1 = new AppParameter();
        //app1.setBitmapDrawable(getResources().getDrawable(R.drawable.bus,));
        Resources res=getResources();
        Drawable drawable = res.getDrawable(R.drawable.bus);
        app1.setAppType("公交卡");
        app1.setBitmapDrawable((BitmapDrawable) drawable);
        AppParameter app2 = new AppParameter();
        drawable = res.getDrawable(R.drawable.card);
        app2.setAppType("银行卡");
        app2.setBitmapDrawable((BitmapDrawable) drawable);
        AppParameter app3 = new AppParameter();
        drawable = res.getDrawable(R.drawable.card);
        app3.setAppType("酒店");
        app3.setBitmapDrawable((BitmapDrawable) drawable);
        //ArrayList<AppParameter> listData = new ArrayList<AppParameter>();
        appAllListData.add(0,app1);
        appAllListData.add(1,app2);
        appAllListData.add(2,app3);
        return appAllListData;
    }

    public  List<AppParameter> getItemDataFromSE(){
        AppParameter app1 = new AppParameter();
        //app1.setBitmapDrawable(getResources().getDrawable(R.drawable.bus,));
        app1.setAppType("公交卡");
        AppParameter app2 = new AppParameter();
        app2.setAppType("银行卡");
//        AppParameter app3 = new AppParameter();
//        app3.setAppText("酒店");
//        ArrayList<AppParameter> listData = new ArrayList<AppParameter>();
        appLocalListData.add(0,app1);
        appLocalListData.add(1,app2);
//        listData.add(2,app3);
        return appLocalListData;
    }

    public static AllApplicationFragment newInstance(int arg){
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT,arg);
        AllApplicationFragment allApplicationFragment = new AllApplicationFragment();
        allApplicationFragment.setArguments(bundle);
        return allApplicationFragment;
    }

    public static final String ARGUMENT="argument";
    public static final String ARGUMENT_APPTYPE="argument_type";
}
