package com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit;

/**
 * Created by SPREADTRUM\ting.long on 16-10-11.
 */

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

final class ScalarRequestBodyConverter<T> implements Converter<T, RequestBody> {
    static final ScalarRequestBodyConverter<Object> INSTANCE = new ScalarRequestBodyConverter<>();
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/xml; charset=utf-8");//("text/plain; charset=UTF-8");

    private ScalarRequestBodyConverter() {
    }

    @Override public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
    }


}
