package com.spreadtrum.iit.zpayapp.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.Buffer;

/**
 * Created by SPREADTRUM\ting.long on 16-8-4.
 */
public class SimpleClientActivity extends AppCompatActivity {
    private Button btnConnect;
    private TextView textView;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            textView.setText((CharSequence) msg.obj);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpleclient);
        btnConnect = (Button) findViewById(R.id.id_btn_connect);
        textView = (TextView) findViewById(R.id.id_tv_content_from_server);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket("10.0.64.120",9875);
                            OutputStream os = socket.getOutputStream();
                            byte[] cmd={0x12,(0xab-128),0x01,0x02,0x03,0x4,0x5,0x6,0x7,0x8,0x9,0xa,0xb,0xc,0x01,0x00,0x00};
                            os.write(cmd);
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String content = bufferedReader.readLine();
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = content;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            textView.setText(e.getMessage());
                        }
                    }
                }).start();

            }
        });


    }
}
