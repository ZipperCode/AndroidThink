package com.think.vpn.packet;

import com.think.vpn.utils.CommonUtil;

import java.nio.ByteBuffer;

public class UDPHeader {

    public static final int SRC_PORT_OFFSET = 0;

    public static final int DEST_PORT_OFFSET = 2;

    public static final int LENGTH_OFFSET = 4;

    public static final int CHECK_SUM_OFFSET = 6;
    /**
     * 数据
     */
    private ByteBuffer mData;
    /**
     * ip数据区偏移地址
     */
    private int mIpHeaderOffset;
    /**
     * UDP数据区偏移地址
     */
    private int mDataOffset;

    public UDPHeader(byte[] data, int ipHeaderOffset) {
        this.mData = ByteBuffer.wrap(data);
        this.mIpHeaderOffset = ipHeaderOffset;
        this.mDataOffset = mIpHeaderOffset + Packet.UDP_HEADER_SIZE;
    }

    public UDPHeader(ByteBuffer data, int ipHeaderOffset){
        this.mData = data;
        this.mIpHeaderOffset = ipHeaderOffset;
        this.mDataOffset = mIpHeaderOffset + Packet.UDP_HEADER_SIZE;
    }

    public UDPHeader setSrcPort(int srcPort){
        this.mData.putShort(mIpHeaderOffset + SRC_PORT_OFFSET,(short) srcPort);
        return calcCheckSum();
    }

    public short getSrcPort(){
        short srcPort = this.mData.getShort(mIpHeaderOffset + SRC_PORT_OFFSET);
        return srcPort;
    }

    public UDPHeader setDestPort(int destPort){
        this.mData.putShort(mIpHeaderOffset + DEST_PORT_OFFSET, (short) destPort);
        return calcCheckSum();
    }

    public short getDestPort(){
        short destPort = this.mData.getShort(mIpHeaderOffset + DEST_PORT_OFFSET);
        return destPort;
    }

    public UDPHeader setTotalLength(int totalLength){
        this.mData.putShort(mIpHeaderOffset + LENGTH_OFFSET, (short) totalLength);
        return calcCheckSum();
    }

    public short getTotalLength(){
        return mData.getShort(mIpHeaderOffset + LENGTH_OFFSET);
    }

    public UDPHeader calcCheckSum(){
        this.mData.rewind();
        int checkSum = 0;
        this.mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET,(short)0);
        while (this.mData.hasRemaining()){
            checkSum += this.mData.getShort();
        }
        checkSum = (checkSum >> 16) + (checkSum & 0xFFFF);
        int hight = checkSum >> 16;
        while (hight > 0){
            checkSum += hight;
            hight = checkSum >> 16;
        }
        this.mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET,(short)~(checkSum & 0xFFFF));
        return this;
    }

    public ByteBuffer data() {
        return mData;
    }

    public byte[] dataByte(){
        return mData.array();
    }

    public int offset(){
        return mDataOffset;
    }



    @Override
    public String toString() {
        return "UDPHeader{" +
                "源端口 = " + getSrcPort() + "\n" +
                "目标端口 = " + getDestPort() +"\n" +
                "长度 = " + getTotalLength() +"\n" +
                "校验和 = 0x" + Integer.toHexString(mData
                .getShort(mIpHeaderOffset + CHECK_SUM_OFFSET)) + "\n" +
                ", mIpHeaderOffset=" + mIpHeaderOffset +
                ", mDataOffset=" + mDataOffset +
                '}';
    }
}
