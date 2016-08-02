package com.spreadtrum.iit.zpayapp.display;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.spreadtrum.iit.zpayapp.R;

/**
 * Created by SPREADTRUM\ting.long on 16-7-29.
 */
public class SpecialApplicationActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_application);
        Bundle bundle = getIntent().getExtras();
        String appType = bundle.getString(AllApplicationFragment.ARGUMENT_APPTYPE);
        SpecialApplicationFragment specialApplicationFragment = SpecialApplicationFragment.newInstance(appType);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.id_special_app,specialApplicationFragment,appType).commit();
    }
}
