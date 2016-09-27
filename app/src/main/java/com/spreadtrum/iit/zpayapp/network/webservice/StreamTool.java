package com.spreadtrum.iit.zpayapp.network.webservice;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class StreamTool {
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
