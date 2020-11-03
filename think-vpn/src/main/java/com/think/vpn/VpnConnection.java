package com.think.vpn;

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
import java.net.Socket;
import java.nio.ByteBuffer;
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

    private final ByteBuffer mReadPacketBuffer;
    /**
     * 本地地址
     */
    private final int mLocalIpAddress;
    /**
     * 数据包类
     */
    private final Packet mPacket;

    private final LocalVpnService mLocalVpnService;

    private boolean isStop = false;

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
        this.mLocalTcpProxyServer = new LocalTcpProxyServer(this, 8888);
        this.mDnsProxy = new DnsProxy(this);
    }

    @Override
    public void run() {
        ThreadManager.getInstance().execPoolFuture(mDnsProxy);
//        ThreadManager.getInstance().execPoolFuture(mLocalTcpProxyServer);
        readVpnPacket();
    }


    public boolean protect(DatagramSocket socket) {
        return mLocalVpnService.protect(socket);
    }

    public boolean protect(Socket socket) {
        return mLocalVpnService.protect(socket);
    }

    public void readVpnPacket() {
        LogUtils.debug("读取数据包");
        try {
            int readSize = 0;
            mFis = new FileInputStream(mParcelFileDescriptor.getFileDescriptor());
            mFos = new FileOutputStream(mParcelFileDescriptor.getFileDescriptor());
            boolean idle = false;
            while (!Thread.interrupted() && !isStop) {
                idle = true;
                while ((readSize = mFis.read(mReadPacketBuffer.array())) > 0) {
                    mReadPacketBuffer.clear();
                    mReadPacketBuffer.limit(readSize);
                    onIpPacketReceived(readSize);
                    idle = false;
                }
                if (idle) {
                    Thread.sleep(10);
                }
            }
        } catch (IOException e) {
            LogUtils.error("IO 异常");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LogUtils.info(TAG, "调用finally方法");
            IoUtils.close(this);
        }
    }

    public void  onIpPacketReceived(int readSize) throws IOException {
        IPHeader ipHeader = mPacket.mIpHeader;
//        ipHeader.mData.limit(readSize);
//        System.out.println("收到ip数据包 ： ip协议类型 = " + ipHeader.getProtocol());
        if (ipHeader.getProtocol() == IPHeader.PROTOCOL_TCP) {
            receiveTcpPacket(ipHeader, readSize);
        } else if (ipHeader.getProtocol() == IPHeader.PROTOCOL_UDP) {
            receiveUdpPacket(ipHeader);
        }
    }

    private void receiveTcpPacket(IPHeader ipHeader, int size) throws IOException {
        TCPHeader tcpHeader = mPacket.mTcpHeader;
        LogUtils.info("解析Tcp数据包，源地址为："
                + CommonUtil.int2Ip(ipHeader.getSourceIpAddress())
                + ":" + tcpHeader.getSrcPort()
                + "目标地址为：" + CommonUtil.int2Ip(ipHeader.getDestAddress()) + ":" + tcpHeader.getDestPort()
                + ",校验ip数据是否正常：" + IPHeader.checkCrc(ipHeader)
                + ",校验Tcp数据是否正常：" + TCPHeader.checkCrc(tcpHeader));
//        LogUtils.error("IP数据报为：" + ipHeader);
        Log.e(TAG, "修改前TCP数据报为 = " + tcpHeader);
        if (ipHeader.getSourceIpAddress() == mLocalIpAddress) {

        }

        if (tcpHeader.getSrcPort() == mLocalTcpProxyServer.getProxyPort()) {
            LogUtils.debug(TAG, "收到本地Tcp代理服务器的数据");
            Session session = mLocalTcpProxyServer.getSession(tcpHeader.getDestPort());
            if (session != null) {
                ipHeader.setSourceAddress(session.remoteIP);
                tcpHeader.setSrcPort(session.remotePort);
                ipHeader.setDestinationAddress(mLocalIpAddress);
                mFos.write(ipHeader.mData.array(), 0, size);
            }
        } else {
            int key = tcpHeader.getSrcPort();
            Session session = mLocalTcpProxyServer.getSession(key);
            if (session == null || session.remoteIP != ipHeader.getDestAddress() || session.remotePort
                    != tcpHeader.getDestPort()) {
                session = mLocalTcpProxyServer.createSession(key, ipHeader.getDestAddress(), tcpHeader
                        .getDestPort());
            }

            session.packetSent++; //注意顺序
            int tcpDataSize = ipHeader.getTotalLen() - ipHeader.getHeaderLength() - tcpHeader.getHeaderLen();
            //丢弃tcp握手的第二个ACK报文。因为客户端发数据的时候也会带上ACK，这样可以在服务器Accept之前分析出HOST信息。
            if (session.packetSent == 2 && tcpDataSize == 0) {
                return;
            }

//            ipHeader.setSourceAddress(ipHeader.getDestAddress());
            ipHeader.setDestinationAddress(mLocalIpAddress);
            tcpHeader.setDestPort(mLocalTcpProxyServer.getProxyPort());
            Log.e(TAG, "修改后TCP数据报为 = " + tcpHeader);
//            LogUtils.error("修改后IP数据报为：" + ipHeader);
            Log.e(TAG, "检查数据包checkSum ： ip = " + IPHeader.checkCrc(ipHeader) + ",tcp = "+ TCPHeader.checkCrc(tcpHeader));
            mFos.write(ipHeader.mData.array(), 0, size);
        }
    }

    private void receiveUdpPacket(IPHeader ipHeader) {
        UDPHeader udpHeader = mPacket.mUdpHeader;
        LogUtils.info("解析UDP数据包，源地址为："
                + CommonUtil.int2Ip(ipHeader.getSourceIpAddress())
                + ":" + udpHeader.getSrcPort()
                + "目标地址为：" + CommonUtil.int2Ip(ipHeader.getDestAddress()) + ":" + udpHeader.getDestPort()
                + ",校验ip数据是否正常：" + IPHeader.checkCrc(ipHeader)
                + ",校验UDP数据是否正常：" + UDPHeader.checkCrc(udpHeader));
        if (ipHeader.getSourceIpAddress() == mLocalIpAddress && udpHeader.getDestPort() == 53) {
//            LogUtils.info("本地发出的udp数据");
            // 获取udp数据，包括头和实际数据
            ByteBuffer data = udpHeader.data();
            // 设置位置到数据部分，后面发送的时候直接发送数据部分
            data.position(udpHeader.offset());
            // 将数据部分进行切分
            ByteBuffer dnsData = data.slice();
            // 标志位复位
            dnsData.clear();
            // 设置数据长度为ip头长度-udp头长度 ipHeader-UdpHeader-Data
            dnsData.limit(ipHeader.getTotalLen() - udpHeader.offset());
            // 构造一个dns数据包
            DnsPacket dnsPacket = DnsPacket.parseFromBuffer(dnsData);
//            Log.d(TAG, "ipHeader ==> " + ipHeader + "udpHeader = " + udpHeader);
//            Log.e(TAG,"请求的dnsPacket = " + dnsPacket);

            if (dnsPacket != null && dnsPacket.mHeader.mQuestionCount > 0) {
                // 将数据转发到Dns代理中
                mDnsProxy.onDnsRequestReceived(ipHeader, udpHeader, dnsPacket);
            }
        }
    }


    public void sendUdpPacket(Packet packet) {
        try {
            Log.e(TAG, "udp checkSum = " + UDPHeader.checkCrc(packet.mUdpHeader) + ",ip数据报总长度 = " + packet.mIpHeader.getTotalLen());
            mFos.write(packet.mData, 0, packet.mIpHeader.getTotalLen());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(TAG, "sendUdpPacket error");
        }
    }

    @Override
    public void close() throws IOException {
        ThreadManager.getInstance().cancelSchedule(mLocalTcpProxyServer);
        ThreadManager.getInstance().cancelSchedule(mDnsProxy);
        IoUtils.close(mFis, mFos, mParcelFileDescriptor);
    }
}
