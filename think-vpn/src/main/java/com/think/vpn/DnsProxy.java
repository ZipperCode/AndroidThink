package com.think.vpn;

import android.util.Log;
import android.util.SparseArray;

import com.think.core.util.LogUtils;
import com.think.vpn.packet.IPHeader;
import com.think.vpn.packet.Packet;
import com.think.vpn.packet.UDPHeader;
import com.think.vpn.packet.dns.DnsPacket;
import com.think.vpn.packet.dns.Question;
import com.think.vpn.packet.dns.Resource;
import com.think.vpn.packet.dns.ResourcePointer;
import com.think.vpn.utils.CommonUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : zzp
 * @date : 2020/9/3
 **/
public class DnsProxy implements Runnable {

    private static final String TAG = DnsProxy.class.getSimpleName();

    private static final ConcurrentHashMap<Integer, String> IPDomainMaps = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> DomainIPMaps = new ConcurrentHashMap<>();

    /* 查询超时（纳秒）*/
    private final long QUERY_TIMEOUT_NS = 10 * 1000000000L;
    /* 数据报缓冲区 */
    private static final byte[] RECEIVE_BUFFER = new byte[2000];

    /**
     * 数据包发送者
     */
    private DatagramSocket mClient;
    /**
     * 每一个查询
     */
    private final SparseArray<QueryState> mQueryArray;
    /**
     * dns头中的transactionId
     */
    private short mQueryID;

    private final VpnConnection mVpnConnection;

    public DnsProxy(VpnConnection vpnConnection) {
        this.mVpnConnection = vpnConnection;
        this.mQueryArray = new SparseArray<QueryState>();
        try {
            this.mClient = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            int headerLength = Packet.IP4_HEADER_SIZE + Packet.UDP_HEADER_SIZE;
            IPHeader ipHeader = new IPHeader(RECEIVE_BUFFER, 0);
            ipHeader.fullDefault();
            UDPHeader udpHeader = new UDPHeader(RECEIVE_BUFFER, Packet.IP4_HEADER_SIZE);
            ByteBuffer dnsBuffer = ByteBuffer.wrap(RECEIVE_BUFFER);
            // 将position移动到数据区
            dnsBuffer.position(headerLength);
            // 分片操作，将同一个byte缓冲区分割成两片，两片缓冲区包含不同的p、c,实现UDP头的共用
            dnsBuffer = dnsBuffer.slice();
            // 定义一个数据报
            DatagramPacket packet = new DatagramPacket(RECEIVE_BUFFER, headerLength, RECEIVE_BUFFER.length - headerLength);
            while (mClient != null && !mClient.isClosed()) {
                // 数据长度应该减去头的长度
                packet.setLength(RECEIVE_BUFFER.length - headerLength);
                mClient.receive(packet);
                dnsBuffer.clear();
                dnsBuffer.limit(packet.getLength());
                try {
                    DnsPacket dnsPacket = DnsPacket.fromBytes(dnsBuffer);
//                    LogUtils.debug(TAG,"收到的dnsBuffer包 = " +dnsPacket);
                    if (dnsPacket != null) {
                        LogUtils.debug("DNS代理解析 ： " + dnsPacket);
                        onDnsResponseReceived(ipHeader, udpHeader, dnsPacket);
                    }
                } catch (Exception e) {
                    LogUtils.error("dns解析错误");
                }
            }
        } catch (IOException e) {
            LogUtils.error(TAG, e.getMessage());
        } finally {
            LogUtils.info(TAG, "finilly 方法被调用，说明线程被中断");
        }
    }

    private void onDnsResponseReceived(IPHeader ipHeader, UDPHeader udpHeader, DnsPacket dnsPacket) {
        QueryState state = null;
        synchronized (mQueryArray) {
            state = mQueryArray.get(dnsPacket.mHeader.mTransactionId);
            if (state != null) {
                mQueryArray.remove(dnsPacket.mHeader.mTransactionId);
            }
        }

        if (state != null) {
            //DNS污染，默认污染海外网站
            dnsPollution(udpHeader.dataByte(), dnsPacket);
            dnsPacket.mHeader.setTransactionId(state.clientQueryId);
            ipHeader.setSourceAddress(state.remoteIp)
                    .setDestinationAddress(state.clientIp)
                    .setProtocol(Packet.UDP_PROTOCOL)
                    .setTotalLen(Packet.IP4_HEADER_SIZE + Packet.UDP_HEADER_SIZE + dnsPacket.mSize);
            udpHeader.setSrcPort(state.remotePort)
                    .setDestPort(state.clientPort)
                    .setTotalLength(Packet.UDP_HEADER_SIZE + dnsPacket.mSize);
            // 将数据包通过VpnService发送出去
            mVpnConnection.sendUdpPacket(new Packet(ipHeader.mData));
        }
    }

    /**
     * 查询是否存在dns污染
     *
     * @param rawPacket 数据包
     * @param dnsPacket dns数据包
     * @return true表示存在dns污染
     */
    private boolean dnsPollution(byte[] rawPacket, DnsPacket dnsPacket) {
        if (dnsPacket.mHeader.mQuestionCount > 0) {
            // dns请求头问题数，查找第一个解析的问题
            Question question = dnsPacket.mQuestions[0];
            // 问题类型为解析ip地址
            if (question.mQueryType == 1) {
                // 获取解析后的ip地址
                int realIp = getFirstIp(dnsPacket);
                // 根据ip表判断是否需要通过代理，如果

//                if (ProxyConfig.Instance.needProxy(question.Domain, realIp)) {
//                    int fakeIP = getOrCreateFakeIp(question.Domain);
//                    tamperDnsResponse(rawPacket, dnsPacket, fakeIP);
//
//                    return true;
//                }
            }
        }
        return false;
    }

    /**
     * 获取第一个DNS数据包中的ip地址
     *
     * @param dnsPacket dns数据包
     * @return ip地址
     */
    private int getFirstIp(DnsPacket dnsPacket) {
        for (int i = 0; i < dnsPacket.mHeader.mAnswerCount; i++) {
            Resource resource = dnsPacket.mAnswers[i];
            if (resource.mQueryType == 1) {
                return resource.getFirstIp();
            }
        }
        return 0;
    }

    private int getOrCreateFakeIp(String domainString) {
        Integer fakeIP = DomainIPMaps.get(domainString);
        if (fakeIP == null) {
            int hashIP = domainString.hashCode();
            do {
                fakeIP = ProxyConfig.FAKE_NETWORK_IP | (hashIP & 0x0000FFFF);
                hashIP++;
            } while (IPDomainMaps.containsKey(fakeIP));

            DomainIPMaps.put(domainString, fakeIP);
            IPDomainMaps.put(fakeIP, domainString);
        }
        return fakeIP;
    }

    /**
     * 串改dns响应头
     *
     * @param rawPacket 数据包
     * @param dnsPacket dns封装的数据包
     * @param newIp     新的ip
     */
    private void tamperDnsResponse(byte[] rawPacket, DnsPacket dnsPacket, int newIp) {
        // 获取dns请求的第一个问题资源
        Question question = dnsPacket.mQuestions[0];
        // 回答数修改为1
        dnsPacket.mHeader.mAnswerCount = (short) 1;
        dnsPacket.mHeader.mAuthorityCount = (short) 0;
        dnsPacket.mHeader.mAdditionalCount = (short) 0;

        // 解析Answer为ResourcePointer
        ResourcePointer rPointer = new ResourcePointer(rawPacket, question.offset() + question.length());
        //
        rPointer.setDomain((short) 0xC00C);
        rPointer.setType(question.mQueryType);
        rPointer.setClass(question.mQueryClass);
        rPointer.setTtl(64);
        rPointer.setDataLength((short) 4);
        rPointer.setIp(newIp);

        dnsPacket.mSize = 12 + question.length() + 16;
    }

    /**
     * 从VpnService发出的UDP请求在这里进行DNS解析
     *
     * @param ipHeader  ip头
     * @param udpHeader udp头
     * @param dnsPacket 数据包
     */
    public void onDnsRequestReceived(IPHeader ipHeader, UDPHeader udpHeader, DnsPacket dnsPacket) {
//        if (!interceptDns(ipHeader, udpHeader, dnsPacket)) {
        QueryState state = new QueryState();
        state.clientQueryId = dnsPacket.mHeader.mTransactionId;
        state.queryNanoTime = System.nanoTime();
        state.clientIp = ipHeader.getSourceIpAddress();
        state.clientPort = udpHeader.getSrcPort();
        state.remoteIp = ipHeader.getDestAddress();
        state.remotePort = udpHeader.getDestPort();

//        LogUtils.debug(TAG,state.toString());
        mQueryID++;
        // dns头在请求是设置一个随机数，在响应的时候返回此随机数
        dnsPacket.mHeader.setTransactionId(mQueryID);

        synchronized (mQueryArray) {
            clearExpiredQueries();
            mQueryArray.put(mQueryID, state);
        }
        ByteBuffer dnsBuffer = ByteBuffer.allocate(dnsPacket.mSize);
        dnsPacket.toBytes(dnsBuffer);

        InetSocketAddress remoteAddress = new InetSocketAddress(CommonUtil.getAddress(state.remoteIp), state.remotePort);
//        DatagramPacket packet = new DatagramPacket(udpHeader.dataByte(), udpHeader.offset(), dnsPacket.mSize);
        DatagramPacket packet = new DatagramPacket(dnsBuffer.array(), dnsPacket.mSize);
        packet.setSocketAddress(remoteAddress);

        try {
            if (mVpnConnection.protect(mClient)) {
                mClient.send(packet);
            } else {
                LogUtils.error(TAG, "VPN protect udp socket failed.");
            }
        } catch (IOException e) {
            LogUtils.error(TAG, e.getMessage());
        }
//        }
    }

    /**
     * 拦截dns请求数据
     *
     * @param ipHeader
     * @param udpHeader
     * @param dnsPacket
     * @return
     */
    private boolean interceptDns(IPHeader ipHeader, UDPHeader udpHeader, DnsPacket dnsPacket) {
        Question question = dnsPacket.mQuestions[0];
        LogUtils.debug(TAG, "Question Query Domain = " + question.mQueryDomain);
        // 1 表示地址
        if (question.mQueryType == 1) {
            // 判断此请求在本地是否存在代理
//            if (ProxyConfig.Instance.needProxy(question.Domain)) {
//                int fakeIP = getOrCreateFakeIP(question.Domain);
//                tamperDnsResponse(ipHeader.mData.array(), dnsPacket, fakeIP);
//
//                // 获取当前请求的ip地址和端口
//                int sourceIp = ipHeader.getSourceIpAddress();
//                short sourcePort = (short) udpHeader.getSrcPort();
//                LogUtils.debug(TAG, "当前发送的IP请求，来自：" + sourceIp + ":" + sourcePort);
//                // 将源和目的相互对换
//                ipHeader.setSourceAddress(ipHeader.getDestAddress());
//                ipHeader.setDestinationAddress(sourceIp);
//                ipHeader.setTotalLen(20 + 8 + dnsPacket.mSize);
//                udpHeader.setSrcPort(udpHeader.getDestPort());
//                udpHeader.setDestPort(sourcePort);
//                udpHeader.setTotalLength(8 + dnsPacket.mSize);
//                // 使用Vpn发送数据包
////                LocalVpnService.Instance.sendUDPPacket(ipHeader, udpHeader);
//                return true;
//            }
        }
        return false;
    }

    private void clearExpiredQueries() {
        long now = System.nanoTime();
        for (int i = mQueryArray.size() - 1; i >= 0; i--) {
            QueryState state = mQueryArray.valueAt(i);
            if ((now - state.queryNanoTime) > QUERY_TIMEOUT_NS) {
                mQueryArray.removeAt(i);
            }
        }
    }



    private static class QueryState {
        /* 客户端查询ID */
        public short clientQueryId;
        /* 查询的Nano时间 */
        public long queryNanoTime;
        /* 客户端ip */
        public int clientIp;
        /* 客户端端口 */
        public short clientPort;
        /* 远程ip */
        public int remoteIp;
        /* 远程端口 */
        public short remotePort;

        @Override
        public String toString() {
            return "QueryState{" +
                    "clientQueryId=" + clientQueryId +
                    ", queryNanoTime=" + queryNanoTime +
                    ", clientIp=" + clientIp +
                    ", clientPort=" + clientPort +
                    ", remoteIp=" + remoteIp +
                    ", remotePort=" + remotePort +
                    '}';
        }
    }
}
