package com.spreadtrum.iit.zpayapp.network.webservice;

/**
 * Created by SPREADTRUM\ting.long on 16-9-12.
 * 获取应用列表结果回调接口
 */
public interface TSMInformationCallback {
    /**
     * 获取应用列表结果回调
     * @param xml 返回的应用列表xml格式结果
     */
    void getAppInfo(String xml);
}
