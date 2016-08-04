package com.spreadtrum.iit.zpayapp.display;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-7-29.
 */
public class SpecialApplicationFragment extends Fragment {
    private static final String ARGUMENT = "ARGUMENT";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter specialApplicationAdapter;
    private String appType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        appType = bundle.getString(ARGUMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_special_application,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.id_recycler_view_special_app);
        //创建GridLayoutManager
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //每一个item的高度是固定的
        recyclerView.hasFixedSize();
        //Textview
        TextView tvTitle = (TextView) view.findViewById(R.id.id_tv_title);
        //创建adapter
        if(appType.compareTo("公交卡")==0){

            specialApplicationAdapter = new CommonAdapter<Card>(view.getContext(), R.layout.item_card, getCardListData(appType)) {
                @Override
                protected void convert(ViewHolder holder, Card card, int position) {
                    holder.setImageResource(R.id.id_iv_card_image,card.getCardView());
                    holder.setText(R.id.id_tv_card_name,card.getCardName());
                }
            };
            tvTitle.setText(appType);
        }
        else if(appType.compareTo("银行卡")==0){
            specialApplicationAdapter = new CommonAdapter<Card>(view.getContext(),R.layout.item_card,getCardListData(appType)) {
                @Override
                protected void convert(ViewHolder holder, Card card, int position) {
                    holder.setImageResource(R.id.id_iv_card_image,card.getCardView());
                    holder.setText(R.id.id_tv_card_name,card.getCardName());
                }
            };
            tvTitle.setText(appType);
        }
        recyclerView.setAdapter(specialApplicationAdapter);
        return view;
    }

    private List<Card> getCardListData(String cardType) {
        List<Card> cardListData = new ArrayList<Card>();
        if(cardType.compareTo("公交卡")==0){
            Card busCard1 = new Card("公交卡",R.drawable.bus,"北京公交一卡通");
            Card busCard2 = new Card("公交卡",R.drawable.bus,"上海公交一卡通");
            cardListData.add(busCard1);
            cardListData.add(busCard2);
        }
        else if(cardType.compareTo("银行卡")==0){
            Card bankCard1 = new Card(cardType,R.drawable.card,"中国工商银行");
            Card bankCard2 = new Card(cardType,R.drawable.card,"中国农业银行");
            cardListData.add(bankCard1);
            cardListData.add(bankCard2);
        }
        else{

        }
        return cardListData;
    }

    public static SpecialApplicationFragment newInstance(String arg){
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT,arg);
        SpecialApplicationFragment specialApplicationFragment = new SpecialApplicationFragment();
        specialApplicationFragment.setArguments(bundle);
        return specialApplicationFragment;
    }
}
