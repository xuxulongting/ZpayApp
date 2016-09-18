package com.spreadtrum.iit.zpayapp.network.webservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;

import java.io.IOException;
import java.io.InputStream;
//import org.ksoap2.transport.HttpTransportSE;
/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class WebserviceActivity extends AppCompatActivity{
    private Button btnTestWebService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTestWebService = (Button) findViewById(R.id.id_test_webservice);
        btnTestWebService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //创建请求xml
                        String seId="451000000000000020160328000000010003";
                        String requestType = "dbquery";
                        String requestData = "applistquery";
                        String requestXml = "<?xml version=\"1.0\" encoding = \"UTF-8\"?>\n" +
                                "<tsmdbrequest version=\"1.0\">\n" +
                                "         <SEIndex>451000000000000020160328000000010003</SEIndex>\n" +
                                "         <reqtype>dbquery</reqtype>\n" +
                                "         <reqdata>applistquery</reqdata>\n" +
                                "</tsmdbrequest>";
                                // MessageBuilder.doBussinessRequest(seId,requestType,requestData);
                        String requestXml2 = "<?xml version=\"1.0\" encoding = \"UTF-8\"?>\n" +
                                "<tsmdbrequest version=\"1.0\">\n" +
                                "         <SEIndex>451000000000000020160328000000010003</SEIndex>\n" +
                                "         <reqtype>dbinsert</reqtype>\n" +
                                "         <reqdata>\n" +
                                "                   <tasktype>D5</tasktype>\n" +
                                "                   <taskcommand>D50101</taskcommand>\n" +
                                "         </reqdata>\n" +
                                "</tsmdbrequest>";
                        String xml1="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZyA9ICJVVEYtOCI/Pgo8dHNtZGJyZXF1ZXN0IHZlcnNpb249IjEuMCI+CiAgICAgICAgIDxTRUluZGV4PjQ1MTAwMDAwMDAwMDAwMDAyMDE2MDMyODAwMDAwMDAxMDAwMzwvU0VJbmRleD4KICAgICAgICAgPHJlcXR5cGU+ZGJxdWVyeTwvcmVxdHlwZT4KICAgICAgICAgPHJlcWRhdGE+YXBwbGlzdHF1ZXJ5PC9yZXFkYXRhPgo8L3RzbWRicmVxdWVzdD4=";
                        String xml2="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZyA9ICJVVEYtOCI/Pgo8dHNtZGJyZXF1ZXN0IHZlcnNpb249IjEuMCI+CiAgICAgICAgIDxTRUluZGV4PjQ1MTAwMDAwMDAwMDAwMDAyMDE2MDMyODAwMDAwMDAxMDAwMzwvU0VJbmRleD4KICAgICAgICAgPHJlcXR5cGU+ZGJpbnNlcnQ8L3JlcXR5cGU+CiAgICAgICAgIDxyZXFkYXRhPgogICAgICAgICAgICAgICAgICAgPHRhc2t0eXBlPkQ1PC90YXNrdHlwZT4KICAgICAgICAgICAgICAgICAgIDx0YXNrY29tbWFuZD5ENTAxMDE8L3Rhc2tjb21tYW5kPgogICAgICAgICA8L3JlcWRhdGE+CjwvdHNtZGJyZXF1ZXN0Pg==";

                        String xml = ApplyPersonalizationService.getTSMAppInformation(xml2, new TSMAppInformationCallback() {
                            @Override
                            public void getAppInfo(String xml) {
                                //String taskIdDecode = new String(Base64.decode(xml.getBytes(),Base64.DEFAULT));
                                //LogUtil.debug(xml);
                            }
                        });

//                        String taskid = new ApplyPersonalizationService().getTaskid("1234","12334","1213123");
//                        String taskIdDecode = new String(Base64.decode(taskid.getBytes(),Base64.DEFAULT));
//                        LogUtil.debug("taskid is:"+taskIdDecode);
                    }
                }).start();

            }
        });
    }
}
