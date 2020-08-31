package com.think.vpn.packet;

import java.nio.ByteBuffer;

public class UDPHeader {

    public static final int SRC_PORT_OFFSET = 0;

    public static final int DEST_PORT_OFFSET = 2;

    public static final int LENGTH_OFFSET = 4;

    public static final int CHECK_SUM_OFFSET = 6;

    private ByteBuffer data;
    private int dataOffset;

    public UDPHeader(byte[] data, int dataOffset) {
        this.data = ByteBuffer.wrap(data);
        this.dataOffset = dataOffset;
    }

    public UDPHeader(ByteBuffer data, int dataOffset){
        this.data = data;
        this.dataOffset = dataOffset;
    }

    public UDPHeader setSrcPort(int srcPort){
        this.data.putShort(SRC_PORT_OFFSET,(short) srcPort);
        return calcCheckSum();
    }

    public int getSrcPort(){
        int srcPort = this.data.getShort(SRC_PORT_OFFSET);
        return srcPort;
    }

    public UDPHeader setDestPort(int destPort){
        this.data.putShort(DEST_PORT_OFFSET, (short) destPort);
        return calcCheckSum();
    }

    public int getDestPort(){
        int destPort = this.data.getShort();
        return destPort;
    }

    public UDPHeader calcCheckSum(){
        this.data.rewind();
        int checkSum = 0;
        this.data.putShort(CHECK_SUM_OFFSET,(short)0);
        while (this.data.hasRemaining()){
            checkSum += this.data.getShort();
        }
        checkSum = (checkSum >> 16) + (checkSum & 0xFFFF);
        int hight = checkSum >> 16;
        while (hight > 0){
            checkSum += hight;
            hight = checkSum >> 16;
        }
        this.data.putShort(CHECK_SUM_OFFSET,(short)~(checkSum & 0xFFFF));
        return this;
    }

}
