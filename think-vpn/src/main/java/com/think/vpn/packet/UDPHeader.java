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

    public int mSize;

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

    public int getTotalLength(){
        return mData.getShort(mIpHeaderOffset + LENGTH_OFFSET) & 0xFFFF;
    }

    public void setCheckSum(short checkSum){
        mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET, checkSum);
    }

    public short getCheckSum(){
        return mData.getShort(mIpHeaderOffset + CHECK_SUM_OFFSET);
    }

    public UDPHeader calcCheckSum(){
        int checkSum = 0;
        this.mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET,(short)0);

        // 伪首部12字节 源地址、目标地址、标志、协议、tcp长度
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(this.mData.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(this.mData.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(this.mData.get(IPHeader.PROTOCOL_OFFSET));
        short udpLength = (short) (this.mData.getShort(IPHeader.TOTAL_LEN_OFFSET) - Packet.IP4_HEADER_SIZE);
        psdHeader.putShort(udpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += (psdHeader.getShort() & 0x0000FFFF);
        }
        ByteBuffer newBuffer = ByteBuffer.wrap(this.mData.array());
        newBuffer.position(mIpHeaderOffset);
        newBuffer.limit(mIpHeaderOffset + getTotalLength());
        ByteBuffer udpBuffer = newBuffer.slice();
        while (udpBuffer.hasRemaining()){
            try {
                checkSum += (udpBuffer.getShort() & 0x0000FFFF);
            } catch (Exception e) {
                checkSum += ((udpBuffer.get() << 8) & 0x0000FFFF);
            }
        }
        while ((checkSum >> 16) > 0){
            checkSum = (checkSum >> 16) + checkSum & 0xFFFF;
        }
        this.mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET,(short)~(checkSum & 0xFFFF));
        return this;
    }

    public static boolean checkCrc(UDPHeader udpHeader){
        long checkSum = 0;
        ByteBuffer buffer = udpHeader.mData;
        buffer.position(udpHeader.mIpHeaderOffset);
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(buffer.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(buffer.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(buffer.get(IPHeader.PROTOCOL_OFFSET));
        short tcpLength = (short) (buffer.getShort(IPHeader.TOTAL_LEN_OFFSET) - Packet.IP4_HEADER_SIZE);
        psdHeader.putShort(tcpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += psdHeader.getShort();
        }

        ByteBuffer udpBuffer = buffer.slice();
        while (udpBuffer.hasRemaining()){
            try {
                checkSum += udpBuffer.getShort() & 0xFFFF;
            } catch (Exception e) {
               checkSum += (udpBuffer.get() << 8) & 0xFFFF;
            }
        }
        int packetCheckSum =  udpHeader.mData.getShort(udpHeader.mIpHeaderOffset + CHECK_SUM_OFFSET) & 0xFFFF;
        checkSum -= packetCheckSum;
        while ((checkSum >> 16) > 0){
            checkSum = (checkSum >> 16) + checkSum & 0xFFFF;
        }
        short sum = (short) ~(checkSum & 0xFFFF);

        return sum == packetCheckSum;
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
                "源端口 = " + getSrcPort() + ",\t" +
                "目标端口 = " + getDestPort() +",\t" +
                "长度 = " + getTotalLength() +",\t" +
                "校验和 = 0x" + Integer.toHexString(mData
                .getShort(mIpHeaderOffset + CHECK_SUM_OFFSET)) + "\t" +
                ", mIpHeaderOffset=" + mIpHeaderOffset +
                ", mDataOffset=" + mDataOffset +
                "}\n";
    }
}
