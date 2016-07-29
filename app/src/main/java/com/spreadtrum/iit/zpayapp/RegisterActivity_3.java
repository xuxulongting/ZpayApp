package com.spreadtrum.iit.zpayapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by SPREADTRUM\ting.long on 16-7-25.
 */
public class RegisterActivity_3 extends AppCompatActivity implements TitleFragment.BackBtnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_3);
        findViewById(R.id.id_btn_next_step_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity_3.this,RegisterActivity_4.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void BackBtnClick() {
        finish();
    }
}
