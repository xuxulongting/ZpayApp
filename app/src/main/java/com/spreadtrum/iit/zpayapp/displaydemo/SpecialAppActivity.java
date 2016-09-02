package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.display.Card;

/**
 * Created by SPREADTRUM\ting.long on 16-9-2.
 */
public class SpecialAppActivity extends AppCompatActivity {

    private TextView textViewAppName;
    private ImageView imageViewIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialapp);
        Intent intent = getIntent();
        Card card = (Card) intent.getSerializableExtra("APP_PARAMETER");
//        Bundle bundle = new Bundle();
//        Card card = intent.getSerializableExtra("APP_PARAMETER");
        String cardName = card.getCardName();
        int resourceIdIcon = card.getCardView();

        textViewAppName = (TextView) findViewById(R.id.id_tv_appname);
        imageViewIcon = (ImageView) findViewById(R.id.id_iv_icon_large);
        textViewAppName.setText(cardName);
        imageViewIcon.setImageResource(resourceIdIcon);

    }
}
