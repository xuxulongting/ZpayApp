package com.spreadtrum.iit.zpayapp.message;

/**
 * Created by SPREADTRUM\ting.long on 16-10-12.
 * TSM返回的APDU指令，用于以xml格式返回的APDU指令
 */

public class APDUInfo {
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getAPDU() {
        return APDU;
    }

    public void setAPDU(String APDU) {
        this.APDU = APDU;
    }

    public String getSW() {
        return SW;
    }

    public void setSW(String SW) {
        this.SW = SW;
    }

    private String index;
    private String APDU;
    private String SW;
}
