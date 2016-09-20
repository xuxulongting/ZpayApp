package com.spreadtrum.iit.zpayapp.network.webservice;

import android.util.Base64;
import android.util.Xml;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;

import org.w3c.dom.ProcessingInstruction;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class ApplyPersonalizationService {
//    public static final String WEBSERVICE_PATH = "http://10.0.64.120:6893/SPRDTSMDbService.asmx";
    public static final String WEBSERVICE_PATH = "http://192.168.1.150:6893/SPRDTSMDbService.asmx";
    /**
     * 查询应用
     * @param xml 查询应用请求xml
     * @return
     */
    public static String getTSMAppInformation(String xml, final TSMAppInformationCallback callback){
        String soap = readSoap("soap11.xml");
        soap = soap.replace("123",xml);
        LogUtil.debug("soap:"+soap);
        byte[] entity = soap.getBytes();
        final String[] tsmXmlArrays = new String[1];
        HttpUtils.sendHttpRequestforWebservice(WEBSERVICE_PATH, entity, new HttpCallbackListener() {
            @Override
            public void onSuccess(InputStream inputStream) {
                LogUtil.debug(inputStream.toString());
                String xmlResultEncode = parseSOAP(inputStream,"TSMBDServiceResult");//"XMLReturnResult");//"TSMBDServiceResponse"
                if(callback!=null){
                    String xmlResult = new String(Base64.decode(xmlResultEncode.getBytes(),Base64.DEFAULT));
                    callback.getAppInfo(xmlResult);
                    //callback.getAppInfo(tsmXmlArrays[0]);
                }
                return ;

            }

            @Override
            public void onError(String errResponse) {
                tsmXmlArrays[0]="";
            }
        });
        return null;
    }

    public String getTaskid(String seid,String appid,String userid){
        String soap = readSoap("soap12.xml");
        soap = soap.replaceAll("\\$seid",seid);
        soap = soap.replaceAll("\\$appid",appid);
        soap = soap.replaceAll("\\$userid",userid);
//        String soap = readSoap("soap.xml");
//        soap = soap.replaceAll("\\$taskid","");
        byte[] entity = soap.getBytes();
        String path = "http://10.0.64.120:7654/WebService1.asmx";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");
            conn.setRequestProperty("Content-Length",String.valueOf(entity.length));
            conn.getOutputStream().write(entity);
            int res = conn.getResponseCode();
            if(res==200){
                //test(conn.getInputStream(),"XMLReturnResult");
                return parseSOAP(conn.getInputStream(),"ApplyPersonalizationResult");

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private void test(InputStream inputStream,String keywords){
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder xmlAsString = new StringBuilder(4098);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                xmlAsString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Look for links using a regex. Assume the first link is "Prev" and the
        // next link is "Next"
        Pattern hrefRegex = Pattern.compile("<XMLReturnResult>\"([^\"]*)\">");
        Matcher m = hrefRegex.matcher(xmlAsString);
        String linkToPrevPost = null;
        String linkToNextPost = null;
        while (m.find()) {
            String hrefValue = m.group(1);
            if (linkToPrevPost == null) {
                linkToPrevPost = hrefValue;
            } else {
                linkToNextPost = hrefValue;
            }
        }
    }

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

    public static void getAppinfoFromWebservice(String seId,String requestType,String requestData,TSMAppInformationCallback callback){
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,requestData);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        ApplyPersonalizationService.getTSMAppInformation(requestXmlBase64,callback);
    }

    public static void getTSMTaskid(String seId, String requestType, RequestTaskidEntity entity, TSMAppInformationCallback callback){
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,entity);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        ApplyPersonalizationService.getTSMAppInformation(requestXmlBase64,callback);
    }
}

