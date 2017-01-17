package com.spreadtrum.iit.zpayapp.network.tcp;

import com.spreadtrum.iit.zpayapp.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by SPREADTRUM\ting.long on 16-8-8.
 */
public class TCPSocket{
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

    /**
     * 获取TCPsocket实例
     * @param netAddr   IP地址
     * @param netPort   端口号
     * @return
     * @throws IOException
     */
    public static TCPSocket getInstance(String netAddr,int netPort){
        if(tcpSocket==null){
            LogUtil.debug("create socket");
            try {
                tcpSocket = new TCPSocket(netAddr,netPort);
            } catch (IOException e) {
//                e.printStackTrace();
                return null;
            }
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
        LogUtil.debug("close socket before");
        if(socket!=null){
            try {
                LogUtil.debug("close socket");
                socket.close();
                socket=null;
                tcpSocket = null;
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
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("BLE",e.getMessage());
            }
        }
        return count;
    }
}
