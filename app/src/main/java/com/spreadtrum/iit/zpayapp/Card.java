package com.spreadtrum.iit.zpayapp;

import android.widget.ImageView;

/**
 * Created by SPREADTRUM\ting.long on 16-7-27.
 */
public class Card {
    public Card(String cardType,int cardViewId,String cardName){
       this.cardType = cardType;
        this.cardViewId = cardViewId;
        this.cardName = cardName;
    }
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
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

    private String cardType;
    private int cardViewId;
    private String cardName;
}
