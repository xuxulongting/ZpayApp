package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SPREADTRUM\ting.long on 16-11-2.
 */

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        TextView mVersion = (TextView) findViewById(R.id.about_version);
        //获取App版本信息
        MyApplication app = MyApplication.getInstance();
//        JSONObject jsonAppInfo = app.getAppInfo();
        PackageInfo info = app.getPackageInfo();
        String versionName = info.versionName;
        mVersion.setText("Version "+versionName);


    }
}
