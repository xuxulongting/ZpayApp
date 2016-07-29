package com.spreadtrum.iit.zpayapp;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by SPREADTRUM\ting.long on 16-7-25.
 */
public class RegisterActivity_2 extends AppCompatActivity implements View.OnClickListener,TitleFragment.BackBtnClickListener {
    private Button btnNextStep2=null;
    private Button btnReSend=null;
    private int authentic_item=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);
        btnNextStep2 = (Button) findViewById(R.id.id_btn_next_step_2);
        btnReSend = (Button) findViewById(R.id.id_btn_resend);
        btnNextStep2.setOnClickListener(this);
        btnReSend.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        final CharSequence[] items = {"1.指纹认证","2.数字密码认证","3.图形密码认证"};
        switch (view.getId()){
            case R.id.id_btn_next_step_2:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("请选择用户认证方式：")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                authentic_item = i;
                                //不能在这里启动新的activity,因为一旦点击item，AlertDialog就结束了。
                            }
            })
                        .create();
                alertDialog.show();
                break;
            case R.id.id_btn_resend:
                break;
        }
        //启动新的activity
        switch(authentic_item){
            case AUTHENTIC_ZHENWEN:
                break;
            case AUTHENTIC_DIGTALPWD:
                Intent intent = new Intent(RegisterActivity_2.this,RegisterActivity_3.class);
                startActivity(intent);
                break;
            case AUTHENTIC_PATTENPWD:
                break;
        }
    }

    public final int AUTHENTIC_ZHENWEN=0;
    public final int AUTHENTIC_DIGTALPWD=1;
    public final int AUTHENTIC_PATTENPWD=2;


    @Override
    public void BackBtnClick() {
        finish();
    }
}
