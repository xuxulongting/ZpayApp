package com.spreadtrum.iit.zpayapp.network.eSe;

import android.content.Context;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;

import org.simalliance.openmobileapi.Channel;
import org.simalliance.openmobileapi.Reader;
import org.simalliance.openmobileapi.SEService;
import org.simalliance.openmobileapi.Session;

import java.io.IOException;

/**
 * Created by SPREADTRUM\ting.long on 16-12-27.
 */

public class SETransaction {
    private SEService _service = null;
    private Session _session = null;
    private static SETransaction seTransaction = null;
    private Channel channel = null;
    private SEPreparedListener sePreparedListener=null;

    private SETransaction(Context context){
        SEServiceCallback callback = new SEServiceCallback();
        new SEService(context,callback);
    }

    public static SETransaction getInstance(Context context){
        if (seTransaction==null)
            seTransaction = new SETransaction(context);
        else
            createThread();
        return seTransaction;
    }

    public static void createThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seTransaction.sePreparedListener.onPrepared();
            }
        }).start();
    }

    public interface SEPreparedListener{
        void onPrepared();
    }
    public void setSePreparedListener(SEPreparedListener listener){
        sePreparedListener = listener;
    }



    private static String bytesToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes)
            sb.append(String.format("%02x ", b & 0xFF));

        return sb.toString();
    }

//    void testBasicChannel(byte[] aid) {
//        try {
////            logText("BasicChannel test: " + ((aid == null) ? "default applet" : bytesToString(aid)) + "\n");
//            Channel channel = _session.openBasicChannel(aid);
//
////            byte[] cmd = new byte[] { (byte) 0x80, (byte) 0xCA, (byte) 0x9F, 0x7F, 0x00 };
//            byte[] cmd = {0x00,(byte)0xA4,0x04,0x00,0x07,(byte)0xA0,0x00,0x00,0x01,0x51,0x00,0x00};
//            LogUtil.debug(" -> " + bytesToString(cmd) + "\n");
//            byte[] rsp = channel.transmit(cmd);
//            LogUtil.debug(" <- " + bytesToString(rsp) + "\n\n");
//
//            byte[] command2 = {(byte)0x80,(byte)0xCA,0x00,0x45,0x00};
//            LogUtil.debug("->" + bytesToString(command2)+"\n");
//            byte[]response2 = channel.transmit(command2);
//            LogUtil.debug("<-" + bytesToString(response2)+"\n\n");
//            channel.close();
//        } catch (Exception e) {
//            LogUtil.debug("Exception on BasicChannel: " + e.getMessage() + "\n\n");
//        }
//    }

    class SEServiceCallback implements SEService.CallBack{
//        private SEPreparedListener callback;
//        public SEServiceCallback(SEPreparedListener callback){
//            this.callback = callback;
//        }

        @Override
        public void serviceConnected(SEService seService) {
            _service = seService;
            setSession(_service);
            if (_session!=null){
                try {
                    channel = _session.openBasicChannel(null);
                    if (sePreparedListener!=null)
                        sePreparedListener.onPrepared();
                } catch (IOException e) {
//                    e.printStackTrace();
                    LogUtil.debug("Exception on BasicChannel: " + e.getMessage());
                }
            }
        }
    }

    public byte[] transactionWitheSE(byte[] cmd){
        byte[] rsp = null;
        if(channel!=null) {
            try {
                rsp = channel.transmit(cmd);
            } catch (IOException e) {
//            e.printStackTrace();
                LogUtil.debug("transmit APDU:" + e.getMessage());
            }
        }
        return rsp;
    }

    private void setSession(SEService seService){
        Reader[] readers = _service.getReaders();
        for (Reader reader : readers)
            LogUtil.debug("	" + reader.getName() + "   - " + ((reader.isSecureElementPresent()) ? "present" : "absent") + "\n");

        if (readers.length == 0) {
            LogUtil.debug("No reader available \n");
            return;
        }
        for (Reader reader : readers) {
            if (!reader.isSecureElementPresent())
                continue;

            LogUtil.debug("\n--------------------------------\nSelected reader: \"" + reader.getName() + "\"\n");

            try {
                _session = reader.openSession();
            } catch (Exception e) {
                LogUtil.debug(e.getMessage());
            }
            if (_session != null)
                return;
        }
    }
}
