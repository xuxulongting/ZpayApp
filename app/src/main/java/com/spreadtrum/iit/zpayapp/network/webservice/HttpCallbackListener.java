package com.spreadtrum.iit.zpayapp.network.webservice;

import java.io.InputStream;

/**
 * Created by SPREADTRUM\ting.long on 16-8-26.
 */
public interface HttpCallbackListener {
    void onSuccess(InputStream inputStream);
    void onError(String errResponse);
}
