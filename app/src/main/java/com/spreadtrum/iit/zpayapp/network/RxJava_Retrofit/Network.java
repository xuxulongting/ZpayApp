package com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit;

import android.database.Observable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by SPREADTRUM\ting.long on 16-10-10.
 */

public class  Network{
    public static TSMService tsmService=null;
//    public static OkHttpClient okHttpClient1 = new OkHttpClient();
    private static okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient();
//    public static  SimpleXmlConverterFactory simpleXmlConverterFactory = SimpleXmlConverterFactory.create();
    public static GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
    public static RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    public static ScalarsConverterFactory scalarsConverterFactory = ScalarsConverterFactory.create();

    public static TSMService getTsmService(){
        if(tsmService==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(WEBSERVICE_PATH)
                    .addConverterFactory(scalarsConverterFactory)
//                    .addConverterFactory(new ToStringConverterFactory())
//                    .addConverterFactory(gsonConverterFactory)
            //Call Adapter Factory 是一个知道如何将 call 实例转换成其他类型的工厂类。目前，我们只有 RxJava 的类型，也就是将 Call 类型转换成 Observable 类型
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            tsmService = retrofit.create(TSMService.class);
        }
        return tsmService;
    }

    public static final String WEBSERVICE_PATH = "http://10.0.64.120:6893/";
}
