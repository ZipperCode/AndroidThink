package com.think.vpn;

import android.net.VpnService;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.think.core.util.IoUtils;
import com.think.core.util.LogUtils;
import com.think.core.util.ThreadManager;
import com.think.vpn.packet.IPHeader;
import com.think.vpn.packet.Packet;
import com.think.vpn.packet.TCPHeader;
import com.think.vpn.packet.UDPHeader;
import com.think.vpn.packet.dns.DnsPacket;
import com.think.vpn.utils.CommonUtil;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author : zzp
 * @date : 2020/8/31
 **/
public class VpnConnection implements Runnable, Closeable {

    private static final String TAG = VpnConnection.class.getSimpleName();

    /**
     * 最大分包大小 MTU = {Short.MAX_VALUE}
     */
    private static final int MAX_PACK_SIZE = Short.MAX_VALUE;

    /**
     * 最大包的大小
     */
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;
    /**
     * 重连等待尝试时间
     */
    private static final long RECONNECT_WAIT_MS = TimeUnit.SECONDS.toMillis(3);
    /* 本地ip地址 */
    public static final String LOCAL_IP_ADDRESS_STR = "10.0.0.2";

    private final ParcelFileDescriptor mParcelFileDescriptor;

    /**
     * 本地TCP代理服务
     */
    private final LocalTcpProxyServer mLocalTcpProxyServer;
    /**
     * DNS代理
     */
    private final DnsProxy mDnsProxy;

    private final String mServerName;
    private final int mServerPort;
    private String proxyHost;
    private int proxyPort;
    /**
     * fd获取的输入输出流
     */
    private FileInputStream mFis;
    private FileOutputStream mFos;

    private ByteBuffer mReadPacketBuffer;
    /**
     * 本地地址
     */
    private int mLocalIpAddress;
    /**
     * 数据包类
     */
    private Packet mPacket;

    private final LocalVpnService mLocalVpnService;

    public VpnConnection(LocalVpnService vpnService,
                         ParcelFileDescriptor mParcelFileDescriptor,
                         String mServerName,
                         int mServerPort,
                         String proxyHost,
                         int proxyPort) {
        mLocalVpnService = vpnService;
        this.mParcelFileDescriptor = mParcelFileDescriptor;
        this.mServerName = mServerName;
        this.mServerPort = mServerPort;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.mReadPacketBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        this.mPacket = new Packet(mReadPacketBuffer);
        this.mLocalIpAddress = CommonUtil.ip2Int(LOCAL_IP_ADDRESS_STR);
        mLocalTcpProxyServer = new LocalTcpProxyServer(0);
        this.mDnsProxy = new DnsProxy(this);
    }

    @Override
    public void run() {
//        try{
//            Log.i(TAG,"开始Socket连接。。。");
//            final SocketAddress serverAddress = new InetSocketAddress(mServerName,mServerPort);
//            for (int attempt = 0; attempt < 3; ++attempt) {
//                // 连接重试
//                if (connect(serverAddress)) {
//                    attempt = 0;
//                }
//                // 休眠后重新连接
//                Thread.sleep(3000);
//            }
//        }catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            Log.e(TAG,"连接出现错误");
//        }
        ThreadManager.getInstance().execPoolFuture(mDnsProxy);
        readPacket();
    }



    public boolean protect(DatagramSocket socket){
        return mLocalVpnService.protect(socket);
    }

    public boolean protect(Socket socket){
        return mLocalVpnService.protect(socket);
    }

    public void readPacket() {
        LogUtils.debug("读取数据包");
        try {
            int readSize = 0;
            mFis = new FileInputStream(mParcelFileDescriptor.getFileDescriptor());
            mFos = new FileOutputStream(mParcelFileDescriptor.getFileDescriptor());
            while (readSize != -1 && !Thread.interrupted()) {
                while ((readSize = mFis.read(mReadPacketBuffer.array())) > 0) {
                    onIpPacketReceived(readSize);
                }
            }
        } catch (IOException e) {
            LogUtils.error("IO 异常");
        } finally {
            LogUtils.info(TAG, "调用finally方法");
            IoUtils.close(this);
        }
    }

    public void onIpPacketReceived(int readSize) {
        IPHeader ipHeader = mPacket.mIpHeader;
//        System.out.println("收到ip数据包");
//        System.out.println(ipHeader);
        if (ipHeader.getProtocol() == IPHeader.PROTOCOL_TCP) {
            TCPHeader tcpHeader = mPacket.mTcpHeader;
            LogUtils.info("解析Tcp数据包");
            LogUtils.info(tcpHeader.toString());


        } else if (ipHeader.getProtocol() == IPHeader.PROTOCOL_UDP) {
            UDPHeader udpHeader = mPacket.mUdpHeader;
            LogUtils.info("解析UDP数据包");
//            LogUtils.info(udpHeader.toString());
            if (ipHeader.getSourceIpAddress() == mLocalIpAddress && udpHeader.getDestPort() == 53) {
                LogUtils.info("本地发出的udp数据");
                ByteBuffer data = udpHeader.data();
                data.position(udpHeader.offset());
                ByteBuffer dnsData = data.slice();
                dnsData.clear();
//                LogUtils.debug(TAG,"ipHeader length = " + ipHeader.getTotalLen());
//                LogUtils.debug(TAG,"udpHeader length = " + udpHeader.getTotalLength());
//                LogUtils.debug(TAG,"udpHeader offset = " + udpHeader.offset());
                dnsData.limit(ipHeader.getTotalLen() - udpHeader.offset());
                DnsPacket dnsPacket = DnsPacket.parseFromBuffer(dnsData);
                LogUtils.debug(TAG,"dnsPacket = " +dnsPacket);
                if (dnsPacket != null && dnsPacket.mHeader.mQuestionCount > 0) {
                    // 将数据转发到Dns代理中
                    mDnsProxy.onDnsRequestReceived(ipHeader, udpHeader, dnsPacket);
                }
            }

        }
    }

    public void sendUdpPacket(Packet packet){
        try {
            mFos.write(packet.mData,packet.mIpHeader.mDataOffset,packet.mIpHeader.getTotalLen());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(TAG,"sendUdpPacket error");
        }
    }

    @Override
    public void close() throws IOException {
        ThreadManager.getInstance().cancelSchedule(mLocalTcpProxyServer);
        ThreadManager.getInstance().cancelSchedule(mDnsProxy);
        IoUtils.close(mFis, mFos, mParcelFileDescriptor);
    }
}
