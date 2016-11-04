package com.spreadtrum.iit.zpayapp.network.webservice;

import android.util.Base64;
import android.util.Xml;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.network.http.HttpUtils;
import com.spreadtrum.iit.zpayapp.network.NetParameter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */


public class TSMPersonalizationWebservice {
//    public static final String WEBSERVICE_PATH = "http://10.0.64.120:6893/SPRDTSMDbService.asmx";

//    public static final String WEBSERVICE_PATH = "http://192.168.1.150:6893/SPRDTSMDbService.asmx";
    /**
     * 获取展示的应用信息
     * @param seId  SE的索引信息
     * @param requestType 请求的类型（数据查询/数据写入）
     * @param requestData 请求的数据内容
     * @param callback     网络请求结果回调
     */
    public static void getAppinfoFromWebservice(String seId,String requestType,String requestData,
                                                TSMAppInformationCallback callback){
        //创建request xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,requestData);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        LogUtil.debug(requestXmlBase64);
        //发送soap请求，并获取xml结果
        getTSMAppInformation(requestXmlBase64,callback);
    }

    /**
     * 获取业务（下载，删除，同步）的任务id
     * @param seId  SE的索引信息
     * @param requestType   请求的类型（数据查询/数据写入）
     * @param entity    请求的数据内容
     * @param callback  网络请求结果回调
     */
    public static void getTSMTaskid(String seId, String requestType, RequestTaskidEntity entity,
                                    TSMAppInformationCallback callback){
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,entity);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        getTSMAppInformation(requestXmlBase64,callback);
    }
    /**
     * 通过web service接口，获取相关信息
     * @param xml 请求xml
     * @return
     */
    private static void getTSMAppInformation(String xml, final TSMAppInformationCallback callback){
        String soap = readSoap("soap11.xml");
        soap = soap.replace("123",xml);
        byte[] entity = soap.getBytes();
        final String[] tsmXmlArrays = new String[1];
        HttpUtils.sendHttpRequestforWebservice(NetParameter.WEBSERVICE_PATH, entity, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.debug(response);
                String xmlResultEncode = parseSOAP(response,"TSMBDServiceResult");//"XMLReturnResult");//"TSMBDServiceResponse"
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
    }

    /**
     * 从webservice响应中提取TSM返回结果（xml格式）
     * @param soapXml   soap xml
     * @param keywords  soap响应结果的tag值
     * @return
     */
    private static String parseSOAP(String soapXml,String keywords){
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(new StringReader(soapXml));
            int eventType = xmlPullParser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if(keywords.equals(nodeName)){
                            return xmlPullParser.nextText();
                        }
                        break;
                }
                eventType=xmlPullParser.next();

            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析soap文件，添加参数
     * @param inputStream
     * @param keywords
     * @return
     */
    private static String parseSOAP(InputStream inputStream,String keywords) {
        XmlPullParser xmlPullParser = Xml.newPullParser();

        try {
            xmlPullParser.setInput(inputStream,"UTF-8");
            String xml = xmlPullParser.toString();
            int eventType = xmlPullParser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if(keywords.equals(nodeName)){
                            //LogUtil.debug("1234",xmlPullParser.nextText());
                            String ret = xmlPullParser.nextText();
                            return ret;//xmlPullParser.nextText();

                        }
                        break;
                }
                eventType=xmlPullParser.next();

            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            LogUtil.debug(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取soap文件
     * @param xmlFile
     * @return
     */
    private static String readSoap(String xmlFile) {
        InputStream inputStream = null;
        try {
            inputStream = MyApplication.getContextObject().getAssets().open(xmlFile);//("soap12_bdservice.xml");//("soap12.xml");
            byte[] data = StreamTool.read(inputStream);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}

