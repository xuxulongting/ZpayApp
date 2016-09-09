package com.spreadtrum.iit.zpayapp.network.webservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

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
                String taskid = new ApplyPersonalizationService().getTaskid("1234","12334","1213123");
                LogUtil.debug("taskid is:"+taskid);
            }
        });
    }
}
