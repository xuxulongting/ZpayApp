package com.spreadtrum.iit.zpayapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.spreadtrum.iit.zpayapp.register.TitleFragment;

/**
 * Created by SPREADTRUM\ting.long on 16-7-25.
 */

public class RegisterActivity_1 extends AppCompatActivity implements TitleFragment.BackBtnClickListener{
    private Fragment titleFragment = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_1);

        findViewById(R.id.id_btn_next_step_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity_1.this,RegisterActivity_2.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void BackBtnClick() {
        finish();
    }
}
