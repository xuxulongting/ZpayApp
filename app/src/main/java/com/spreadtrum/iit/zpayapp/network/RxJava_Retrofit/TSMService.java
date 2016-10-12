package com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import rx.Observable;


import retrofit2.http.POST;


/**
 * Created by SPREADTRUM\ting.long on 16-10-10.
 */

public interface TSMService {
    @GET("/")
    Observable<String> getString();

    @POST("SPRDTSMDbService.asmx")
    Observable<String> getString(@Body String xml);
}
