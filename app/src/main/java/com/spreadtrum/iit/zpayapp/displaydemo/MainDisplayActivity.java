package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.spreadtrum.iit.zpayapp.R;


/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class MainDisplayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindisplay);
        AppStoreFragment appStoreFragment = new AppStoreFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.id_main,appStoreFragment,"AppStoreFragment").commit();
    }
}
