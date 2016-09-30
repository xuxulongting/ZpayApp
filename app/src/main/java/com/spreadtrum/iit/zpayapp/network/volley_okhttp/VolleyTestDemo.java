package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;

import java.util.Map;

/**
 * Created by SPREADTRUM\ting.long on 16-9-28.
 */
public class VolleyTestDemo extends AppCompatActivity {
    private Button btnTestVolley;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTestVolley = (Button) findViewById(R.id.id_test_volley);
        btnTestVolley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://10.0.64.120:6893/SPRDTSMDbService.asmx";
                RequestQueue queue = Volley.newRequestQueue(VolleyTestDemo.this);
                 StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.debug(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.warn(error.getMessage());
                    }
                }){
                     @Override
                     protected Map<String, String> getParams() throws AuthFailureError {
                         return super.getParams();
                     }
                 };
                queue.add(stringRequest);
            }
        });
    }
}
