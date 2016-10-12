package com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.network.http.HttpCallbackListener;
import com.spreadtrum.iit.zpayapp.network.http.HttpUtils;
import com.spreadtrum.iit.zpayapp.network.webservice.StreamTool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by SPREADTRUM\ting.long on 16-10-10.
 */

public class TestActivity extends AppCompatActivity {
    private Button btnTest;

    private Observer<String> observer = new Observer<String>() {
//        @Override
//        public void onCompleted() {
//            LogUtil.debug("onCompleted");
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            LogUtil.debug("onError"+e.getMessage());
//        }
//
//        @Override
//        public void onNext(ResponseBody responseBody) {
//            LogUtil.debug(responseBody.toString());
//        }

        @Override
        public void onCompleted() {
            LogUtil.debug("onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            LogUtil.debug("onError"+e.getMessage());
        }

        @Override
        public void onNext(String s) {
            LogUtil.debug(s);
        }
    };

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

    public static String test(String xml){
        String soap = readSoap("soap11.xml");
        soap = soap.replace("123",xml);
       return soap;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTest = (Button) findViewById(R.id.id_test_volley);
        btnTest.setOnClickListener(new View.OnClickListener() {
            String xml = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZyA9ICJVVEYtOCI/Pgo8dHNtZGJyZXF1ZXN0IHZlcnNpb249IjEuMCI+CiAgICAgICAgIDxTRUluZGV4PjQ1MTAwMDAwMDAwMDAwMDAyMDE2MDMyODAwMDAwMDAxMDAwMzwvU0VJbmRleD4KICAgICAgICAgPHJlcXR5cGU+ZGJpbnNlcnQ8L3JlcXR5cGU+CiAgICAgICAgIDxyZXFkYXRhPgogICAgICAgICAgICAgICAgICAgPHRhc2t0eXBlPkQ1PC90YXNrdHlwZT4KICAgICAgICAgICAgICAgICAgIDx0YXNrY29tbWFuZD5ENTAxMDE8L3Rhc2tjb21tYW5kPgogICAgICAgICA8L3JlcWRhdGE+CjwvdHNtZGJyZXF1ZXN0Pg==";

            @Override
            public void onClick(View view) {
                TSMService tsmService = Network.getTsmService();
                tsmService.getString(test(xml))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
//                HttpUtils.sendHttpRequest("http://10.0.64.120:6893/SPRDTSMDbService.asmx", "", new HttpCallbackListener() {
//                    @Override
//                    public void onSuccess(InputStream inputStream) {
//                        String charset = "UTF-8";
//                        BufferedInputStream bis = new BufferedInputStream(inputStream);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        int c = 0;
//                        byte[] buffer = new byte[8 * 1024];
//                        try {
//                            while ((c = bis.read(buffer)) != -1) {
//                                baos.write(buffer, 0, c);
//                                try {
//                                    baos.flush();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        String result = null;
//                        try {
//                            result = new String(baos.toByteArray(), charset);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        LogUtil.info("响应报文："+result);
//                    }
//
//                    @Override
//                    public void onError(String errResponse) {
//                        LogUtil.debug(errResponse);
//                    }
//                });
            }
        });
    }
}
