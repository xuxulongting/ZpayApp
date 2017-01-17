package com.spreadtrum.iit.zpayapp.network.webservice;

import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.network.http.HttpUtils;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.CustomStringRequest;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */


public class TSMPersonalizationWebservice {
     /**
     * 通过webservice获取远程管理信息
     * @param xml 客户端业务发起数据
     *            具体说明文档在《TSM后台服务与客户端之间通信协议修改说明20161216.docx》
     */
    public static void getRemoteManagementInfoFromWebservice(String xml){
        String soap = SoapXmlBuilder.readSoap("soap11_rm.xml");
        LogUtil.debug("HEARTBEAT","xml:"+xml);
//      //test
//        String testXml = "<?xml version='1.0' encoding='UTF-8' ?><tsm version=\"01\"><clientInfo clientType=\"1\" clientVer=\"1\" /><terminalInfo><seid>45105350524400041600000300000001FFFF</seid><imei></imei><phone></phone></terminalInfo><request type=\"3\"><sessionID>17322137192762123419855228</sessionID><taskID></taskID></request><MAC></MAC></tsm>";
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(xml.getBytes(),Base64.DEFAULT);
        LogUtil.debug("HEARTBEAT",requestXmlBase64);

        soap = soap.replace("123",requestXmlBase64);//xml);//xml);
        LogUtil.debug("HEARTBEAT","soap:"+soap);
        byte[] entity = soap.getBytes();
        HttpUtils.sendHttpRequestforWebservice(NetParameter.WEBSERVICE_RM_PATH, entity, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.debug("HEARTBEAT","response:"+response);
                String responseXml = SoapXmlBuilder.parseSOAP(response,"TSM_RMWS_InterfaceResult");
                byte[] decodeResponseXml = Base64.decode(responseXml.getBytes(),Base64.DEFAULT);
                LogUtil.debug(new String(decodeResponseXml));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.debug("HEARTBEAT","response:"+error.getMessage());
            }
        });
//        HttpUtils.sendHttpRequestforWebservice(NetParameter.WEBSERVICE_RM_PATH, entity, new HttpCallbackListener() {
//            @Override
//            public void onSuccess(InputStream inputStream) {
//                LogUtil.debug("HEARTBEAT","response:"+inputStream.toString());
//            }
//
//            @Override
//            public void onError(String errResponse) {
//                LogUtil.debug("HEARTBEAT","response:"+errResponse);
//            }
//        });
    }
    /**
     * 通过web service接口，获取applet列表信息或task id
     * @param xml 请求xml,根据不同的请求，获取applet列表或者task id
     * @return
     */
    public static void getTSMInformation(String xml, final TSMInformationCallback callback){
        String soap = SoapXmlBuilder.readSoap("soap11.xml");
        soap = soap.replace("123",xml);
        byte[] entity = soap.getBytes();
        final String[] tsmXmlArrays = new String[1];
        //使用volley+okhttp发起网络请求，请求webservice服务
        CustomStringRequest request = new CustomStringRequest(Request.Method.POST, NetParameter.WEBSERVICE_APPLIST_PATH, entity, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.debug(response);
                String xmlResultEncode = SoapXmlBuilder.parseSOAP(response,"TSMBDServiceResult");//"XMLReturnResult");//"TSMBDServiceResponse"
                if(callback!=null){
                    String xmlResult = new String(Base64.decode(xmlResultEncode.getBytes(),Base64.DEFAULT));
                    callback.getAppInfo(xmlResult);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse==null){
                    callback.getAppInfo("");
                }
                else {
                    int errCode = error.networkResponse.statusCode;
                    LogUtil.debug("webservice error:" + String.valueOf(errCode));
                    if (errCode == 808) {
                        callback.getAppInfo(String.valueOf(808));
                    }
                }
            }
        });
        RequestQueueUtils.getInstance().addToRequestQueue(request);
    }
}

