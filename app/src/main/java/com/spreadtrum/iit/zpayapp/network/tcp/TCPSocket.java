package com.spreadtrum.iit.zpayapp.network.tcp;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by SPREADTRUM\ting.long on 16-8-8.
 */
public class TCPSocket implements Runnable{
    private Socket socket;
    private static TCPSocket tcpSocket;

    private TCPSocket(String netAddr,int netPort) throws IOException {
        socket = new Socket(netAddr,netPort);
    }

    private TCPSocket(String netAddr, int netPort, int timeout){
        try {
            socket = new Socket(netAddr,netPort);
            socket.setSoTimeout(timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TCPSocket getInstance(String netAddr,int netPort) throws IOException {
        if(tcpSocket==null){
            tcpSocket = new TCPSocket(netAddr,netPort);
        }
        return tcpSocket;
    }

    public static TCPSocket getInstance(String netAddr,int netPort, int timeout){
        if(tcpSocket==null){
            tcpSocket = new TCPSocket(netAddr,netPort,timeout);
        }
        return tcpSocket;
    }

    public void closeSocket(){
        if(socket!=null){
            try {
                socket.close();
                socket=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public boolean tcpSocketWrite(byte[] bData) throws IOException {
        if(socket!=null) {
            OutputStream os = socket.getOutputStream();
            os.write(bData);
            return true;
        }
        return false;
    }

    public String tcpSocketReadString() throws IOException {
        if(socket!=null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //String content = bufferedReader.readLine();
            //new ClientThread(bufferedReader).start();
            StringBuilder stringBuilder = new StringBuilder();
            String content = null;
            String line = null;
            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            content = stringBuilder.toString();
            return content;

        }
        return null;
    }

    public char[] tcpSocketReadChar()
    {
        int count = 0;
        char[] readBuf = new char[100];
        if (socket != null) {

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                count = bufferedReader.read(readBuf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return readBuf;
    }

    public int tcpSocketReadByte(byte []readBuf)
    {
        int count = 0;
        //byte[] readBuf = new byte[100];
        if (socket != null) {

            try {
                InputStream inputStream = socket.getInputStream();
                count = inputStream.read(readBuf);
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                count = bufferedReader.read(readBuf);
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("BLE",e.getMessage());
            }
        }
        return count;
    }

    @Override
    public void run() {
        int count = 0;
        byte[] readBuf = new byte[100];
        if (socket != null) {

            try {
                InputStream inputStream = socket.getInputStream();
                count = inputStream.read(readBuf);

            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("BLE",e.getMessage());
            }
        }
    }
    public static String TAG = "BLE";
}
