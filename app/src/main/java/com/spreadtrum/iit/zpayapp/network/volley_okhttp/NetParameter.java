package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import retrofit2.http.PUT;

/**
 * Created by SPREADTRUM\ting.long on 16-11-2.
 */

public class NetParameter {

//    public static final String WEBSERVICE_PATH = "http://10.0.70.31:7788/TSMwebservice";
    public static final String LOGIN_URL="http://10.0.73.106:8080/TSM/zx/login";//http://10.0.70.31:7788/register";
    public static final String TSM_URL = "http://10.0.70.31:7788/TSM";
    public static final String REGISTER_URL="http://10.0.73.106:8080/TSM/zx/register";//http://10.0.70.31:7788/register";
    public static final String LOGOUT_URL="http://10.0.70.31:7788/logout";
//    public static final String LOGOUT_URL = "http://10.0.70.93:8080/TSM/zx/logout";
//    192.168.1.150:9875
    public static final String WEBSERVICE_APPLIST_PATH = "http://192.168.1.150:6893/SPRDTSMDbService.asmx";

    public static final String WEBSERVICE_RM_PATH = "http://192.168.1.150:6894/SPRD_TSM_RMService.asmx";
}
