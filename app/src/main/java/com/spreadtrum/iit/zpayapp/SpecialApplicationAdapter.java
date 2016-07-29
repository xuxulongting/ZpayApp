package com.spreadtrum.iit.zpayapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-7-29.
 */
public class SpecialApplicationAdapter extends RecyclerView.Adapter<SpecialApplicationAdapter.ViewHolder> {
    private List<Card> cardListData;
    public SpecialApplicationAdapter(List<Card> cardListData){
        this.cardListData = cardListData;
    }

    //创建itemview
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false);
        return new ViewHolder(view);
    }

    //将数据与界面绑定
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(cardListData.get(position).getCardName());
    }
    //获取item数量
    @Override
    public int getItemCount() {
        return cardListData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;;
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.id_iv_card_image);
            textView = (TextView) itemView.findViewById(R.id.id_tv_card_name);
        }
    }
}
