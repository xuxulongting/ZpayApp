package com.spreadtrum.iit.zpayapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by SPREADTRUM\ting.long on 16-7-27.
 */
public class UserZhiwenLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvCancel = null;
    private TextView tvUsepwd = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiwen_login);
        tvCancel = ((TextView) findViewById(R.id.id_tv_cancel));
        tvUsepwd = (TextView) findViewById(R.id.id_tv_usepwd);
        tvCancel.setOnClickListener(this);
        tvUsepwd.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()){
            case R.id.id_tv_cancel:
                //finish();
                //test
                intent = new Intent(UserZhiwenLoginActivity.this,ApplicationActivity.class);
                startActivity(intent);
                break;
            case R.id.id_tv_usepwd:
                intent = new Intent(UserZhiwenLoginActivity.this,UserDigitalPwdLoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
