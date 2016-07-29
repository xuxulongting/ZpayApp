package com.spreadtrum.iit.zpayapp;

import android.app.Application;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-7-28.
 */
public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> implements View.OnClickListener {
    private List<AppParameter> appDataList;
    public ApplicationAdapter(List<AppParameter> listData){
        appDataList = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(appDataList.get(position).getAppType());
        //holder.imageView.setImageDrawable(bitmapDrawables[position]);
        holder.itemView.setTag(appDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return appDataList.size();
    }

    @Override
    public void onClick(View view) {
        if(itemClickListener!=null){
            itemClickListener.onItemClick(view,(AppParameter)view.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_tv_app_name);
            imageView = (ImageView) itemView.findViewById((R.id.id_iv_app_icon));
            //bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view,AppParameter appData);
        void onItemLongClick(View view,AppParameter appData);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
