package com.spreadtrum.iit.zpayapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by SPREADTRUM\ting.long on 16-7-25.
 */
public class RegisterActivity_4 extends AppCompatActivity implements TitleFragment.BackBtnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_4);
        findViewById(R.id.id_btn_next_step_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog alertDialog = new AlertDialog.Builder(RegisterActivity_4.this).
                        setIcon(R.drawable.verify).
                        setTitle("").
                        setMessage("请填写本人真实身份，认证通过后无法修改").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void BackBtnClick() {
        finish();
    }
}
