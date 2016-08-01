package com.spreadtrum.iit.zpayapp.display;

import android.app.Fragment;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-7-28.
 */
public class AllApplicationFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ApplicationAdapter appAdapterAll;
    private ApplicationAdapter appAdapterLocal;
    private int tabItem = -1;
    private Button btnDeleteApp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            tabItem = bundle.getInt(ARGUMENT);
        }
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
            appAdapterLocal = new ApplicationAdapter(getItemDataFromSE());
            recyclerView.setAdapter(appAdapterLocal);
//            //删除应用按钮
//            btnDeleteApp = (Button) view.findViewById(R.id.id_btn_delete_app);
//            btnDeleteApp.setVisibility(View.VISIBLE);
//            btnDeleteApp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
            //已下载应用中的点击事件
            appAdapterLocal.setOnItemClickListener(new ApplicationAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, AppParameter appData) {
                    Intent intent = new Intent(frgview.getContext(),SpecialApplicationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ARGUMENT_APPTYPE,appData.getAppType());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, AppParameter appData) {

                }
            });
        }
        else  {
            appAdapterAll = new ApplicationAdapter(getItemDataFromTSM());
            recyclerView.setAdapter(appAdapterAll);
            //添加点击事件
            //全部应用中的点击事件
            appAdapterAll.setOnItemClickListener(new ApplicationAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, AppParameter appData) {
                    Toast.makeText(view.getContext(),appData.getAppType(),Toast.LENGTH_LONG).show();

                }

                @Override
                public void onItemLongClick(View view, AppParameter appData) {

                }
            });
        }


        return frgview;
    }

    public List<AppParameter> getItemDataFromTSM(){
        AppParameter app1 = new AppParameter();
        //app1.setBitmapDrawable(getResources().getDrawable(R.drawable.bus,));
        app1.setAppType("公交卡");
        AppParameter app2 = new AppParameter();
        app2.setAppType("银行卡");
        AppParameter app3 = new AppParameter();
        app3.setAppType("酒店");
        ArrayList<AppParameter> listData = new ArrayList<AppParameter>();
        listData.add(0,app1);
        listData.add(1,app2);
        listData.add(2,app3);
        return listData;
    }

    public  List<AppParameter> getItemDataFromSE(){
        AppParameter app1 = new AppParameter();
        //app1.setBitmapDrawable(getResources().getDrawable(R.drawable.bus,));
        app1.setAppType("公交卡");
        AppParameter app2 = new AppParameter();
        app2.setAppType("银行卡");
//        AppParameter app3 = new AppParameter();
//        app3.setAppText("酒店");
        ArrayList<AppParameter> listData = new ArrayList<AppParameter>();
        listData.add(0,app1);
        listData.add(1,app2);
//        listData.add(2,app3);
        return listData;
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
