package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.spreadtrum.iit.zpayapp.R;

/**
 * Created by SPREADTRUM\ting.long on 16-11-1.
 */

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);
        view.setBackgroundResource(R.drawable.yindao1);
        setContentView(view);
        //渐变展示启动界面
        AlphaAnimation animation = new AlphaAnimation(0.5f,1.0f);
        animation.setDuration(1500);
        view.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                goToMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 回到主界面
     */
    public void goToMainActivity(){
        Intent intent = new Intent(this,MainDisplayActivity.class);
        startActivity(intent);
        finish();
    }
}
