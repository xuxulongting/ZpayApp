package com.spreadtrum.iit.zpayapp.network.tcp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by SPREADTRUM\ting.long on 16-8-11.
 */
public class TCPByteRequest extends Thread{
    private final TCPResponse.Listener<byte[]> mListener;
    private final TCPResponse.ErrorListener mErrorListener;
    private final TCPSocket mTCPSocket;
    private byte[] mRequest;

    public boolean isbSeResponse() {
        return bSeResponse;
    }

    public void setbSeResponse(boolean bSeResponse) {
        this.bSeResponse = bSeResponse;
    }

    private boolean bSeResponse = false;

    public TCPByteRequest(TCPSocket tcpSocket, byte[] bRequest, TCPResponse.Listener<byte[]> listener, TCPResponse.ErrorListener mErrorListener){
        this.mListener = listener;
        this.mTCPSocket = tcpSocket;
        this.mRequest = bRequest;
        this.mErrorListener = mErrorListener;
    }
    @Override
    public void run() {
        try {
            if(mTCPSocket.tcpSocketWrite(mRequest)){
                byte []readBuf = new byte[280];
                int readCount = mTCPSocket.tcpSocketReadByte(readBuf);
                mListener.onResponse(readBuf,readCount);
            }
            else
                mErrorListener.onErrorResponse("tcpSocketWrite error");
        } catch (IOException e) {
            e.printStackTrace();
            mErrorListener.onErrorResponse(e.getMessage());
        }
    }
}
