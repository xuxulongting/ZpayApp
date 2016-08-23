package com.spreadtrum.iit.zpayapp.network.nfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.LogUtil;
import com.spreadtrum.iit.zpayapp.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by SPREADTRUM\ting.long on 16-8-22.
 */
public class NFCDemoActivity extends AppCompatActivity implements View.OnClickListener,NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private Button btnReadNFC;
    private Button btnWriteNFC;
    private Button btnSetttingNFC;
    // NFC适配器
    private NfcAdapter nfcAdapter = null;
    // 传达意图
    private PendingIntent pendingIntent = null;
    private PendingIntent pi = null;
    // 滤掉组件无法响应和处理的Intent
    private IntentFilter tagDetected = null;
    // 是否支持NFC功能的标签
    private boolean isNFC_support = false;
    // 文本控件
    private TextView promt = null;

    private Tag tagFromIntent;

    private IntentFilter []intentFiltersArray;
    private String [][]techListsArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcdemo);
        btnReadNFC = (Button) findViewById(R.id.id_btn_readnfc);
        btnWriteNFC = (Button) findViewById(R.id.id_btn_writenfc);
        btnSetttingNFC = (Button) findViewById(R.id.id_btn_setting);
        promt = (TextView) findViewById(R.id.id_tv_display);
        btnWriteNFC.setOnClickListener(this);
        btnReadNFC.setOnClickListener(this);
        //打开NFC
        initNfcData();
        //注册NDEF回调消息
        if(nfcAdapter!=null) {
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        //if(isNFC_support==true)
        //    init_NFC();
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*"); /* Handles all MIME based dispatches.You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef,};
        techListsArray = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNFC_support==false)
            return;
//        startNFCListener();
//        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent()
//                .getAction())) {
//            // 注意这个if中的代码几乎不会进来，因为刚刚在上一行代码开启了监听NFC连接，下一行代码马上就收到了NFC连接的intent，这种几率很小
//            // 处理该intent
//            processIntent(this.getIntent());
//        }
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
//            processIntent(getIntent());

//        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopNFC_Listener();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if(nfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
//            LogUtil.debug(TAG,"receive ACTION_TECH_DISCOVERED");
//            processIntent(intent);
//        }
        //setIntent(intent);
        //Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //processIntent(intent);
    }

    /*private void processIntent(Intent intent) {
        if(isNFC_support==false)
            return;
        // 取出封装在intent中的TAG
        tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        promt.setTextColor(Color.BLUE);
        String metaInfo = "";
        metaInfo += "卡片ID：" + bytesToHexString(tagFromIntent.getId()) + "\n";
        Toast.makeText(this, "找到卡片", Toast.LENGTH_SHORT).show();

        // Tech List
        String prefix = "android.nfc.tech.";
        String[] techList = tagFromIntent.getTechList();

        //分析NFC卡的类型： Mifare Classic/UltraLight Info
        String CardType = "";
        for (int i = 0; i < techList.length; i++) {
            if (techList[i].equals(NfcA.class.getName())) {
                // 读取TAG
                NfcA mfc = NfcA.get(tagFromIntent);
                try {
                    if ("".equals(CardType))
                        CardType = "MifareClassic卡片类型 \n 不支持NDEF消息 \n";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (techList[i].equals(MifareUltralight.class.getName())) {
                MifareUltralight mifareUlTag = MifareUltralight
                        .get(tagFromIntent);
                String lightType = "";
                // Type Info
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        lightType = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        lightType = "Ultralight C";
                        break;
                }
                CardType = lightType + "卡片类型\n";

                Ndef ndef = Ndef.get(tagFromIntent);
                CardType += "最大数据尺寸:" + ndef.getMaxSize() + "\n";

            }
        }
        metaInfo += CardType;
        promt.setText(metaInfo);
    }*/

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        promt.setText(new String(msg.getRecords()[0].getPayload()));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.id_btn_readnfc:
                break;
            case R.id.id_btn_writenfc:
                try {
                    write(tagFromIntent);
                } catch (IOException e) {
                    //e.printStackTrace();
                    promt.setText(promt.getText()+"错误："+e.getMessage()+"\n");
                } catch (FormatException e) {
                    //e.printStackTrace();
                    promt.setText(promt.getText()+"错误："+e.getMessage()+"\n");
                }
                break;
            case R.id.id_btn_setting:
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);
                break;
        }
    }

    private void initNfcData(){
        isNFC_support = true;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        String metaInfo="";
        if(nfcAdapter==null){
            metaInfo = "设备不支持NFC设备";
            Toast.makeText(this,metaInfo,Toast.LENGTH_LONG).show();
            LogUtil.debug(TAG,metaInfo);
            isNFC_support = false;
        }
        if(!nfcAdapter.isEnabled()){
            metaInfo = "请在系统设置中启用NFC";
            Toast.makeText(this,metaInfo,Toast.LENGTH_LONG).show();
            LogUtil.debug(TAG,metaInfo);
            isNFC_support = false;
        }


    }

    private void init_NFC(){
        // 初始化PendingIntent，当有NFC设备连接上的时候，就交给当前Activity处理
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // 新建IntentFilter，使用的是第二种的过滤机制
        tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void startNFCListener(){
        if(nfcAdapter!=null)
            nfcAdapter.enableForegroundDispatch(this,pi,new IntentFilter[]{tagDetected},null);
    }

    private void stopNFC_Listener() {
        // 停止监听NFC设备是否连接
        if (nfcAdapter!=null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    // 字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        return bytesToHexString(src, true);
    }

    private String bytesToHexString(byte[] src, boolean isPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isPrefix == true) {
            stringBuilder.append("0x");
        }
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.toUpperCase(Character.forDigit(
                    (src[i] >>> 4) & 0x0F, 16));
            buffer[1] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,
                    16));
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    // 读取方法
    private String read(Tag tag) throws IOException, FormatException {
        if (tag != null) {
            //解析Tag获取到NDEF实例
            Ndef ndef = Ndef.get(tag);
            //打开连接
            ndef.connect();
            //获取NDEF消息
            NdefMessage message = ndef.getNdefMessage();
            //将消息转换成字节数组
            byte[] data = message.toByteArray();
            //将字节数组转换成字符串
            String str = new String(data, Charset.forName("UTF-8"));
            //关闭连接
            ndef.close();
            return str;
        } else {
            Toast.makeText(NFCDemoActivity.this, "设备与nfc卡连接断开，请重新连接...",
                    Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    // 写入方法
    private void write(Tag tag) throws IOException, FormatException {
        if (tag != null) {
            //新建NdefRecord数组，本例中数组只有一个元素
            NdefRecord[] records = { createRecord() };
            //新建一个NdefMessage实例
            NdefMessage message = new NdefMessage(records);
            // 解析TAG获取到NDEF实例
            Ndef ndef = Ndef.get(tag);
            // 打开连接
            ndef.connect();
            // 写入NDEF信息
            ndef.writeNdefMessage(message);
            // 关闭连接
            ndef.close();
            promt.setText(promt.getText() + "写入数据成功！" + "\n");
        } else {
            Toast.makeText(NFCDemoActivity.this, "设备与nfc卡连接断开，请重新连接...",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //返回一个NdefRecord实例
    private NdefRecord createRecord() throws UnsupportedEncodingException {
        //组装字符串，准备好你要写入的信息
        String msg = "BEGIN:VCARD\n" + "VERSION:2.1\n" + "中国湖北省武汉市\n"
                + "武汉大学计算机学院\n" + "END:VCARD";
        //将字符串转换成字节数组
        byte[] textBytes = msg.getBytes();
        //将字节数组封装到一个NdefRecord实例中去
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/x-vCard".getBytes(), new byte[] {}, textBytes);
        return textRecord;
    }

    private MediaPlayer ring() throws Exception, IOException {
        // TODO Auto-generated method stub
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(this, alert);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            player.setLooping(false);
            player.prepare();
            player.start();
        }
        return player;
    }

    public static String TAG="NFCDemoActivity";

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        Time time = new Time();
        time.setToNow();
        String text = ("Beam me up!\n\n" +
                "Beam Time: " + time.format("%H:%M:%S"));
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/com.example.android.beam", text.getBytes())
                });
        return msg;
    }
    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private static final int MESSAGE_SENT = 1;
}
