package com.spreadtrum.iit.zpayapp.display;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by SPREADTRUM\ting.long on 16-7-27.
 */
public class Card implements Serializable{
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



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getIntroction() {
        return introction;
    }

    public void setIntroction(String introction) {
        this.introction = introction;
    }

    private String cardType;//所属行业
    private int cardViewId;//icon
    private String cardName;//卡名称或者应用名称
    private String status;//绑卡 or 已绑定
    private String capacity;//applet大小
    private String introction;//简介说明


}
