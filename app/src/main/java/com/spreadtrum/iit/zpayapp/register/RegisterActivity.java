package com.spreadtrum.iit.zpayapp.register;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.spreadtrum.iit.zpayapp.display.ApplicationActivity;
import com.spreadtrum.iit.zpayapp.R;

/**
 * Created by SPREADTRUM\ting.long on 16-7-26.
 */
public class RegisterActivity extends AppCompatActivity implements RegisterFragment_1.NextStep_1ClickListener,
        RegisterFragment_2.NextStep_2ClickListener,RegisterFragment_3.NextStep_3ClickListener,
        RegisterFragment_4.NextStep_4ClickListener,
        TitleFragment.BackBtnClickListener {
    private Fragment registerFragment_1=null;
    private Fragment registerFragment_2=null;
    private Fragment registerFragment_3=null;
    private Fragment registerFragment_4=null;
    private int authentic_item = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        getDefaultFragment();
    }

    void getDefaultFragment(){
        registerFragment_1 = new RegisterFragment_1();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(R.id.id_frg_register,registerFragment_1,"FRAGMENT_1").commit();
    }

    @Override
    public void nextStep_1BtnClick() {
        registerFragment_2 = new RegisterFragment_2();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.id_frg_register,registerFragment_2,"FRAGMENT_2");
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void nextStep_2BtnClick() {

        final CharSequence[] items = {"1.指纹认证","2.数字密码认证","3.图形密码认证"};
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("请选择用户认证方式：")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        authentic_item = i;
                        switch(authentic_item) {
                            case AUTHENTIC_ZHENWEN:
                                break;
                            case AUTHENTIC_DIGTALPWD:
                                registerFragment_3 = new RegisterFragment_3();
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.id_frg_register, registerFragment_3, "FRAGMENT_3");
                                ft.addToBackStack(null);
                                ft.commit();
                                break;
                            case AUTHENTIC_PATTENPWD:
                                break;
                        }
                    }
                })
                .create();
        alertDialog.show();

    }

    @Override
    public void nextStep_3BtnClick() {
        registerFragment_4 = new RegisterFragment_4();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.id_frg_register,registerFragment_4,"FRAGMENT_4");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void nextStep_4BtnClick() {
        Dialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).
                setIcon(R.drawable.verify).
                setTitle("").
                setMessage("请填写本人真实身份，认证通过后无法修改").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(RegisterActivity.this,ApplicationActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        alertDialog.show();
    }

    @Override
    public void BackBtnClick() {
        //回退
        FragmentManager fm = getFragmentManager();
        //将当前的事务退出回退栈
        fm.popBackStack();
    }

    public final int AUTHENTIC_ZHENWEN=0;
    public final int AUTHENTIC_DIGTALPWD=1;
    public final int AUTHENTIC_PATTENPWD=2;
}
