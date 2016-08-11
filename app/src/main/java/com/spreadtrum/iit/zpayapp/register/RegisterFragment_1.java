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
public class RegisterFragment_1 extends Fragment implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        if(getActivity() instanceof NextStep_1ClickListener)
            ((NextStep_1ClickListener)getActivity()).nextStep_1BtnClick();
    }

    public interface NextStep_1ClickListener{
        void nextStep_1BtnClick();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_register_1,container,false);
        view.findViewById(R.id.id_btn_next_step_1).setOnClickListener(this);
        return view;
    }
}
