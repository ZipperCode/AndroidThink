package com.think.vpn.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Packet {
    /**
     * IP头大小
     */
    public static final int IP4_HEADER_SIZE = 20;
    /**
     * TCP 头大小
     */
    public static final int TCP_HEADER_SIZE = 20;
    /**
     * UDP头大小
     */
    public static final int UDP_HEADER_SIZE = 8;

    public static final int TCP_PROTOCOL = 6;

    public static final int UDP_PROTOCOL = 17;

    public IPHeader mIpHeader;

    public TCPHeader mTcpHeader;

    public UDPHeader mUdpHeader;

    public boolean isTCP;

    public byte[] mData;

    public Packet(ByteBuffer data){
        this.mData = data.array();
        mIpHeader = new IPHeader(mData,IP4_HEADER_SIZE);
        mTcpHeader = new TCPHeader(mData,IP4_HEADER_SIZE);
        mUdpHeader = new UDPHeader(mData,IP4_HEADER_SIZE);
    }


    @Override
    public String toString() {
        return isTCP ? "Packet{" +
                "mIpHeader=" + mIpHeader +
                ", mTcpHeader=" + mTcpHeader +
                ", mData=" + Arrays.toString(mData) +
                '}':
                "Packet{" +
                        "mIpHeader=" + mIpHeader +
                        ", mUdpHeader=" + mUdpHeader +
                        ", mData=" + Arrays.toString(mData) +
                        '}';
    }
}
