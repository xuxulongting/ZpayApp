package com.spreadtrum.iit.zpayapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SPREADTRUM\ting.long on 16-7-26.
 */
public class RegisterFragment_2 extends Fragment implements View.OnClickListener {

    public interface NextStep_2ClickListener{
        void nextStep_2BtnClick();
        //void reSendCodeBtnClick();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register_2,container,false);
        view.findViewById(R.id.id_btn_next_step_2).setOnClickListener(this);
        view.findViewById(R.id.id_btn_resend).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.id_btn_next_step_2:
                if(getActivity() instanceof NextStep_2ClickListener)
                    ((NextStep_2ClickListener)getActivity()).nextStep_2BtnClick();
                break;
            case R.id.id_btn_resend:
                break;
        }

    }
}
