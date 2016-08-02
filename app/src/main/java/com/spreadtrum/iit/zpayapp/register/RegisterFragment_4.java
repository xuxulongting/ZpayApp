package com.spreadtrum.iit.zpayapp.register;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spreadtrum.iit.zpayapp.R;

/**
 * Created by SPREADTRUM\ting.long on 16-7-26.
 */
public class RegisterFragment_4 extends Fragment implements View.OnClickListener {

    public interface NextStep_4ClickListener{
        void nextStep_4BtnClick();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register_4,container,false);
        view.findViewById(R.id.id_btn_next_step_4).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(getActivity() instanceof NextStep_4ClickListener){
            ((NextStep_4ClickListener)getActivity()).nextStep_4BtnClick();
        }
    }
}
