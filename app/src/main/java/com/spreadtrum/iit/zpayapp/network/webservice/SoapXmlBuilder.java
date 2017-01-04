package com.spreadtrum.iit.zpayapp.network.webservice;

import android.util.Xml;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class SoapXmlBuilder {
//    public static byte[] read(InputStream inStream){
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] buf = new byte[1024];
//        int len = 0;
//        try {
//            while ((len=inStream.read(buf))!=-1){
////                LogUtil.debug("InputStream read len is:"+len);
//                outStream.write(buf,0,len);
//            }
//            inStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return outStream.toByteArray();
//    }

    /**
     * 从webservice响应中提取TSM返回结果（xml格式）
     * @param soapXml   soap xml
     * @param keywords  soap响应结果的tag值
     * @return
     */
    public static String parseSOAP(String soapXml,String keywords){
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
    public static String parseSOAP(InputStream inputStream,String keywords) {
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
    public static String readSoap(String xmlFile) {
        InputStream inputStream = null;
        try {
            inputStream = MyApplication.getContextObject().getAssets().open(xmlFile);//("soap12_bdservice.xml");//("soap12.xml");
            byte[] data = read(inputStream);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将InputStream转换成byte[]
     * @param inStream
     * @return
     */
    public static byte[] read(InputStream inStream){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        try {
            while ((len=inStream.read(buf))!=-1){
//                LogUtil.debug("InputStream read len is:"+len);
                outStream.write(buf,0,len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

}
