package com.spreadtrum.iit.zpayapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Fragment titleFragment=null;
    private Button btn_register=null;
    private Button btn_login=null;
    private Intent intent = null;
    private int authentic_item=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        btn_register = (Button) findViewById(R.id.id_btn_register);
        btn_login = (Button) findViewById(R.id.id_btn_login);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.id_btn_login:
//                CharSequence[] items = {"1.指纹登录","2.数字密码登录","3.图形密码登录"};
//                AlertDialog alertDialog = new AlertDialog.Builder(this)
//                        .setTitle("请选择登录方式:")
//                        .setItems(items, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                authentic_item = i;
//                                switch (authentic_item){
//                                    case AUTHENTIC_ZHENWEN:
//                                        break;
//                                    case AUTHENTIC_DIGTALPWD:
//                                        intent = new Intent(LoginActivity.this,UserDigitalPwdLoginActivity.class);
//                                        startActivity(intent);
//                                        break;
//                                    case AUTHENTIC_PATTENPWD:
//                                        break;
//                                }
//
//                            }
//                        })
//                        .create();
//                alertDialog.show();
                intent = new Intent(LoginActivity.this,UserZhiwenLoginActivity.class);
                startActivity(intent);
                break;

        }

    }
    public final int AUTHENTIC_ZHENWEN=0;
    public final int AUTHENTIC_DIGTALPWD=1;
    public final int AUTHENTIC_PATTENPWD=2;

}
