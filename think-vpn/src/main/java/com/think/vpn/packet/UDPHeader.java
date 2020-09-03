package com.think.vpn.packet;

import java.nio.ByteBuffer;

public class UDPHeader {

    public static final int SRC_PORT_OFFSET = 0;

    public static final int DEST_PORT_OFFSET = 2;

    public static final int LENGTH_OFFSET = 4;

    public static final int CHECK_SUM_OFFSET = 6;

    private ByteBuffer mData;
    private int mIpHeaderOffset;
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
        this.mData.putShort(mDataOffset + SRC_PORT_OFFSET,(short) srcPort);
        return calcCheckSum();
    }

    public int getSrcPort(){
        int srcPort = this.mData.getShort(mDataOffset + SRC_PORT_OFFSET);
        return srcPort;
    }

    public UDPHeader setDestPort(int destPort){
        this.mData.putShort(mDataOffset + DEST_PORT_OFFSET, (short) destPort);
        return calcCheckSum();
    }

    public int getDestPort(){
        int destPort = this.mData.getShort(mDataOffset + DEST_PORT_OFFSET);
        return destPort;
    }

    public UDPHeader calcCheckSum(){
        this.mData.rewind();
        int checkSum = 0;
        this.mData.putShort(mDataOffset + CHECK_SUM_OFFSET,(short)0);
        while (this.mData.hasRemaining()){
            checkSum += this.mData.getShort();
        }
        checkSum = (checkSum >> 16) + (checkSum & 0xFFFF);
        int hight = checkSum >> 16;
        while (hight > 0){
            checkSum += hight;
            hight = checkSum >> 16;
        }
        this.mData.putShort(mDataOffset + CHECK_SUM_OFFSET,(short)~(checkSum & 0xFFFF));
        return this;
    }

}
