package com.spreadtrum.iit.zpayapp.bussiness;

import com.spreadtrum.iit.zpayapp.message.APDUInfo;

/**
 * Created by SPREADTRUM\ting.long on 16-11-4.
 */

public interface TransactionCallback {
    void onTransactionSuccess(APDUInfo apduInfo);
    void onTransactionFailed(APDUInfo apduInfo);
}
