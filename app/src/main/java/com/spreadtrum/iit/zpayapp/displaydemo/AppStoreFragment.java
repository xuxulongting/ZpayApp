package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.display.Card;
//import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

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
   //private List<Card> listBankCardParameter = new ArrayList<Card>();
   // private ListView listViewAppStore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            protected void convert(com.zhy.adapter.abslistview.ViewHolder viewHolder, Card card, int position) {
                viewHolder.setImageResource(R.id.id_iv_icon,card.getCardView());
                viewHolder.setText(R.id.id_tv_name,card.getCardName());
                viewHolder.setText(R.id.id_tv_type,card.getCardType());
            }
        };
        listViewAppStore.setAdapter(busAdapter);

        return view;
    }

    public void getCardListData() {
        Card busCard1 = new Card("旅行交通",R.drawable.bus,"北京公交一卡通");
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
}
