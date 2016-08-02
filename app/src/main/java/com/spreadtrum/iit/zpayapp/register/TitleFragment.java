package com.spreadtrum.iit.zpayapp.register;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.spreadtrum.iit.zpayapp.R;

import java.util.zip.Inflater;

/**
 * Created by SPREADTRUM\ting.long on 16-7-26.
 */
public class TitleFragment extends Fragment implements View.OnClickListener {
    private ImageButton btnBack;

    public interface BackBtnClickListener{
        void BackBtnClick();    //接口类只有全局常量和公共抽象方法
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title_fragment,container,false);
        btnBack = (ImageButton) view.findViewById(R.id.id_imagebtn_back);
        btnBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if((getActivity()) instanceof BackBtnClickListener){
            ((BackBtnClickListener)getActivity()).BackBtnClick();
        }
    }
}
