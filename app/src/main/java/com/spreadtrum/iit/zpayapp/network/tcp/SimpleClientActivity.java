package com.spreadtrum.iit.zpayapp.network.tcp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.LogUtil;
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
            //textView.setText((CharSequence) msg.obj);
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
                            TCPSocket tcpSocket = TCPSocket.getInstance(NetParameter.IPAddress,NetParameter.Port);
                            String strSeId = "451000000000000020160328000000010003";
                            byte[] maskId = {0x12,(byte)0xab};
                            byte[] randomNum = {0x01,0x02,0x03,0x4,0x5,0x6,0x7,0x8,0x9,0xa,0xb,0xc};
                            byte[] cmd={0x01,0x00,0x39,0x24};
                            byte[] bSeNO = strSeId.getBytes();
                            byte[] id = {0x2E,0x6E};
                            byte[] input = new byte[2+12+1+2+57];
                            byte[] taskId = new byte[20];
                            System.arraycopy(id,0,taskId,18,id.length);
                            System.arraycopy(maskId,0,input,0,maskId.length);
                            System.arraycopy(randomNum,0,input,maskId.length,randomNum.length);
                            System.arraycopy(cmd,0,input,maskId.length+randomNum.length,cmd.length);
                            System.arraycopy(bSeNO,0,input,maskId.length+randomNum.length+cmd.length,bSeNO.length);
                            System.arraycopy(taskId,0,input,maskId.length+randomNum.length+cmd.length+bSeNO.length,taskId.length);
                            tcpSocket.tcpSocketWrite(input);
                            for(int i=0;i<input.length;i++)
                                LogUtil.debug("TEST",input[i]+"");
                            LogUtil.debug("TEST",bytesToHexString(input));
                            LogUtil.debug("TEST","------------------");
//                            byte[] output = tcpSocket.tcpSocketRead();
//                            for(int i=0;i<output.length;i++)
//                                LogUtil.debug("TEST",output[i]+"");
//                            LogUtil.debug("TEST",bytesToHexString(output));
//                            Message msg = new Message();
//                            msg.what = 0;
//                            msg.obj = output;
//                            handler.sendMessage(msg);

                            byte []buf=new byte[100];
                            int readCount = tcpSocket.tcpSocketReadByte(buf);
                            LogUtil.debug("TEST","readCount is "+readCount);
                            LogUtil.debug("TEST",bytesToHexString(buf));

                        } catch (IOException e) {
                            e.printStackTrace();
                            textView.setText(e.getMessage());
                        }
                    }
                }).start();

            }
        });


    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }
}
