package com.spreadtrum.iit.zpayapp.network.http;

import java.io.InputStream;

/**
 * Created by SPREADTRUM\ting.long on 16-9-27.
 */
public interface HttpResponseCallback<T> {
    void onSuccess(T response);
    void onError(String errResponse);
}
