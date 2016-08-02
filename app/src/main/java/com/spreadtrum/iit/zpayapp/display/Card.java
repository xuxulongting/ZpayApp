package com.spreadtrum.iit.zpayapp.display;

import android.widget.ImageView;

/**
 * Created by SPREADTRUM\ting.long on 16-7-27.
 */
public class Card {
    public Card(String cardType,int cardViewId,String cardName){
       this.appType = cardType;
        this.cardViewId = cardViewId;
        this.cardName = cardName;
    }
    public String getAppType() {
        return appType;
    }

    public void setAppType(String cardType) {
        this.appType = cardType;
    }

    public int getCardView() {
        return cardViewId;
    }

    public void setCardView(int cardViewId) {
        this.cardViewId = cardViewId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    private String appType;
    private int cardViewId;
    private String cardName;
}
