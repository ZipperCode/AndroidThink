package com.think.vpn;

import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.think.core.util.IoUtils;
import com.think.core.util.LogUtils;
import com.think.vpn.packet.IPHeader;
import com.think.vpn.packet.Packet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * @author : zzp
 * @date : 2020/8/31
 **/
public class VpnConnection implements Runnable {

    private static final String TAG = VpnConnection.class.getSimpleName();

    /**
     * 最大分包大小 MTU = {Short.MAX_VALUE}
     */
    private static final int MAX_PACK_SIZE = Short.MAX_VALUE;

    /** 最大包的大小 */
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;
    /** 重连等待尝试时间 */
    private static final long RECONNECT_WAIT_MS = TimeUnit.SECONDS.toMillis(3);

    private final ParcelFileDescriptor mParcelFileDescriptor;
    private final String mServerName;
    private final int mServerPort;
    private String proxyHost;
    private int proxyPort;

    private FileInputStream mFis;
    private FileOutputStream mFos;

    private ByteBuffer mReadPacketBuffer;

    private Packet mPacket;

    public VpnConnection(ParcelFileDescriptor mParcelFileDescriptor, String mServerName, int mServerPort, String proxyHost, int proxyPort) {
        this.mParcelFileDescriptor = mParcelFileDescriptor;
        this.mServerName = mServerName;
        this.mServerPort = mServerPort;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.mReadPacketBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        this.mPacket = new Packet(mReadPacketBuffer);
    }

    @Override
    public void run() {
        try{
            Log.i(TAG,"开始Socket连接。。。");
            final SocketAddress serverAddress = new InetSocketAddress(mServerName,mServerPort);
            for (int attempt = 0; attempt < 3; ++attempt) {
                // 连接重试
                if (connect(serverAddress)) {
                    attempt = 0;
                }
                // 休眠后重新连接
                Thread.sleep(3000);
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG,"连接出现错误");
        }
        try {
            int readSize = 0;
            mFis = new FileInputStream(mParcelFileDescriptor.getFileDescriptor());
            mFos = new FileOutputStream(mParcelFileDescriptor.getFileDescriptor());
            while (readSize != -1){
                while ((readSize = mFis.read(mReadPacketBuffer.array())) > 0){
                    onIpPacketReceived(readSize);
                }
            }
        }catch (IOException e){
            LogUtils.error("IO 异常");
        }

    }

    public void dispose(){
        IoUtils.close(mFos,mFis,mParcelFileDescriptor);
    }

    private boolean connect(SocketAddress socketAddress) throws IOException{
        return false;
    }

    public void onIpPacketReceived(int readSize){
        if(mPacket.mIpHeader.getProtocol() == IPHeader.PROTOCOL_TCP){

        }else if(mPacket.mIpHeader.getProtocol() == IPHeader.PROTOCOL_UDP){
            
        }
    }





}
