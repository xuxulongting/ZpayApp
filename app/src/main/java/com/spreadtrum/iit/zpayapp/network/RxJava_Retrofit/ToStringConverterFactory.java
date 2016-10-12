package com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit;

/**
 * Created by SPREADTRUM\ting.long on 16-10-11.
 */

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Kamil on 2016-01-13.
 Corrected version, It works properly with the latest retrofit
 retrofit:2.0.0-beta3'
 */
public class ToStringConverterFactory extends Converter.Factory {
    private static final MediaType MEDIA_TYPE_WEBSERVICE = MediaType.parse("text/xml; charset=utf-8");//("text/plain");


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, String>() {
                @Override
                public String convert(ResponseBody value) throws IOException {
                    return value.string();
                }
            };
        }
        return null;
    }


    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {

        if (String.class.equals(type)) {
            return new Converter<String, RequestBody>() {
                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MEDIA_TYPE_WEBSERVICE, value);
                }
            };
        }
        return null;
    }
}
