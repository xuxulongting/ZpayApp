package com.spreadtrum.iit.zpayapp;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by SPREADTRUM\ting.long on 16-7-28.
 */
public class ApplicationActivity extends AppCompatActivity implements View.OnClickListener {
    private AllApplicationFragment allAppFragment;
    private AllApplicationFragment localAppFragment;
    private Button btnAllApp;
    private Button btnLocalApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        btnAllApp = (Button) findViewById(R.id.id_btn_all_app);
        btnLocalApp = (Button) findViewById(R.id.id_btn_local_app);
        btnAllApp.setOnClickListener(this);
        btnLocalApp.setOnClickListener(this);
        getFragment(TAB_ALL_APP);
    }

    void getFragment(int app){
        if(app==TAB_ALL_APP)
            allAppFragment = AllApplicationFragment.newInstance(TAB_ALL_APP);
        else
            allAppFragment = AllApplicationFragment.newInstance(TAB_LOCAL_APP);
        FragmentManager fm = getFragmentManager();
        //replace相当于remove & add,使用另一个Fragment替换当前的，实际上就是remove()然后add()的合体
        fm.beginTransaction().replace(R.id.id_app,allAppFragment,"ALL_APPLICATION").commit();
        }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_all_app:
                //allAppFragment.onDetach();
                getFragment(TAB_ALL_APP);
                break;
            case R.id.id_btn_local_app:
                //allAppFragment.onDetach();
                getFragment(TAB_LOCAL_APP);
                break;
        }

    }

    public static final int TAB_ALL_APP = 1;
    public static final int TAB_LOCAL_APP = 2;
}
