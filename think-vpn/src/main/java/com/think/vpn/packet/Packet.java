package com.think.vpn.packet;

import java.nio.ByteBuffer;

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

    private ByteBuffer mByteBuffer;


    public Packet(ByteBuffer data){
        this.mByteBuffer = data;
        mIpHeader = new IPHeader(mByteBuffer.array(),IP4_HEADER_SIZE);
        mTcpHeader = new TCPHeader(mByteBuffer.array(),IP4_HEADER_SIZE);
        mUdpHeader = new UDPHeader(mByteBuffer.array(),IP4_HEADER_SIZE);
    }


}
