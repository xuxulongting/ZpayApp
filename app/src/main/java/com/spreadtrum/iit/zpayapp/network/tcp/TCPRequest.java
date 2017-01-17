package com.spreadtrum.iit.zpayapp.network.tcp;

import java.io.IOException;

/**
 * Created by SPREADTRUM\ting.long on 16-9-14.
 */
public class TCPRequest {

    /**
     *通过IP地址与端口号连接TSM，发送数据，并获取响应
     * @param tcpSocket   socket实例
     * @param bRequest  请求数据
     * @param listener  获取正确响应数据回调方法
     * @param errorListener 获取错误响应数据回调方法
     */
    public void TCPByteRequest(final TCPSocket tcpSocket, final byte[] bRequest, final TCPResponse.Listener<byte[]> listener,
                               final TCPResponse.ErrorListener errorListener) {
        if(tcpSocket==null){
            errorListener.onErrorResponse("tcpSocket is null.");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
//                TCPSocket tcpSocket = null;
                try {
//                    tcpSocket = TCPSocket.getInstance(TCPNetParameter.IPAddress,TCPNetParameter.Port);
                    if(tcpSocket.tcpSocketWrite(bRequest)){
                        byte []readBuf = new byte[300];
                        int readCount = tcpSocket.tcpSocketReadByte(readBuf);
                        listener.onResponse(readBuf,readCount);
                    }
                    else
                        errorListener.onErrorResponse(" error");
                } catch (IOException e) {
//                    e.printStackTrace();
                    errorListener.onErrorResponse(e.getMessage());
                }
            }
        }).start();
    }
}
