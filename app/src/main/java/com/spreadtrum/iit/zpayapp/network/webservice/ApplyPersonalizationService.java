package com.spreadtrum.iit.zpayapp.network.webservice;

import android.content.Context;
import android.util.Xml;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class ApplyPersonalizationService {
    public String getTaskid(String seid,String appid,String userid){
        String soap = readSoap();
        soap = soap.replaceAll("\\$seid",seid);
        soap = soap.replaceAll("\\$appid",appid);
        soap = soap.replaceAll("\\$userid",userid);
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
            if(conn.getResponseCode()==200){
                return parseSOAP(conn.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String parseSOAP(InputStream inputStream) {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            xmlPullParser.setInput(inputStream,"UTF-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if("string".equals(nodeName)){
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

    private String readSoap() {
        InputStream inputStream = null;
        try {
            inputStream = MyApplication.getContextObject().getAssets().open("soap12.xml");
            byte[] data = StreamTool.read(inputStream);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        InputStream is= MyApplication.getContextObject().getClass().getClassLoader().getResourceAsStream("assets/soap12_applypersonalization.xml");
//        InputStream instream = getClass().getResourceAsStream("123.txt");
//          InputStream instream = ApplyPersonalizationService.class.getClassLoader()
//                .getResourceAsStream("123.txt");//("soap12_applypersonalization.xml");
//        InputStream instream;
//        try {
//
//            instream = new FileInputStream("/home/local/SPREADTRUM/ting.long/123.txt");//"soap12_applypersonalization.xml");
//            byte[] data = StreamTool.read(instream);
//             return new String(data);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        String url = this.getClass().getClassLoader().getResource("").getPath();
//        LogUtil.debug(url);
//        File f = new File("/home/123456.txt");
//        f.setWritable(true);
//        if (f.isFile()) {
//            try {
//                InputStream is = new FileInputStream(f);
//                byte[] data = StreamTool.read(is);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            try {
//                boolean b = f.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        String s_xmlpath="com/spreadtrum/iit/zpayapp/soap12_applypersonalization.xml";
//        File f = new File(s_xmlpath);
//        if(f.isFile()){
//            LogUtil.debug("123");
//        }
//        ClassLoader classLoader=this.getClass().getClassLoader();
//        InputStream in=classLoader.getResourceAsStream(s_xmlpath);
        return null;
    }
}

