package com.think.vpn.packet;

import java.nio.ByteBuffer;

public class UDPHeader {

    public static final int SRC_PORT_OFFSET = 0;

    public static final int DEST_PORT_OFFSET = 2;

    public static final int LENGTH_OFFSET = 4;

    public static final int CHECK_SUM_OFFSET = 6;
    /**
     * 数据
     */
    private final byte[] mData;
    private final ByteBuffer mDataBuffer;
    /**
     * ip数据区偏移地址
     */
    private final int mIpHeaderOffset;
    /**
     * UDP数据区偏移地址
     */
    private final int mDataOffset;

    public int mSize;

    public UDPHeader(byte[] data, int ipHeaderOffset) {
        this.mData = data;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(ipHeaderOffset);
        mDataBuffer = buffer.slice();
        this.mIpHeaderOffset = ipHeaderOffset;
        this.mDataOffset = mIpHeaderOffset + Packet.UDP_HEADER_SIZE;
    }

    public UDPHeader setSrcPort(int srcPort) {
        this.mDataBuffer.putShort(SRC_PORT_OFFSET, (short) srcPort);
        return calcCheckSum();
    }

    public short getSrcPort() {
        short srcPort = this.mDataBuffer.getShort(SRC_PORT_OFFSET);
        return srcPort;
    }

    public UDPHeader setDestPort(int destPort) {
        this.mDataBuffer.putShort( DEST_PORT_OFFSET, (short) destPort);
        return calcCheckSum();
    }

    public short getDestPort() {
        short destPort = this.mDataBuffer.getShort( DEST_PORT_OFFSET);
        return destPort;
    }

    public UDPHeader setTotalLength(int totalLength) {
        this.mDataBuffer.putShort( LENGTH_OFFSET, (short) totalLength);
        return calcCheckSum();
    }

    public int getTotalLength() {
        return mDataBuffer.getShort( LENGTH_OFFSET) & 0xFFFF;
    }

    public void setCheckSum(short checkSum) {
        mDataBuffer.putShort( CHECK_SUM_OFFSET, checkSum);
    }

    public short getCheckSum() {
        return mDataBuffer.getShort( CHECK_SUM_OFFSET);
    }

    public UDPHeader calcCheckSum() {
        long checkSum = 0;
        this.mDataBuffer.putShort( CHECK_SUM_OFFSET, (short) 0);
        ByteBuffer buffer = ByteBuffer.wrap(mData);
        // 伪首部12字节 源地址、目标地址、标志、协议、tcp长度
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(buffer.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(buffer.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(buffer.get(IPHeader.PROTOCOL_OFFSET));
        short udpLength = (short) (buffer.getShort(IPHeader.TOTAL_LEN_OFFSET) - mIpHeaderOffset);
        psdHeader.putShort(udpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += (psdHeader.getShort() & 0xFFFF);
        }
        buffer.position(mIpHeaderOffset);
        buffer.limit(buffer.getShort(IPHeader.TOTAL_LEN_OFFSET));
        ByteBuffer udpBuffer = buffer.slice();
        while (udpBuffer.hasRemaining()) {
            try {
                checkSum += (udpBuffer.getShort() & 0xFFFF);
            } catch (Exception e) {
                checkSum += ((udpBuffer.get() << 8) & 0xFFFF);
            }
        }
        while ((checkSum >> 16) > 0) {
            checkSum = (checkSum >> 16) + checkSum & 0xFFFF;
        }
        this.mDataBuffer.putShort(CHECK_SUM_OFFSET, (short) ~(checkSum & 0xFFFF));
        return this;
    }

    public static boolean checkCrc(UDPHeader udpHeader) {
        long checkSum = 0;
        ByteBuffer buffer = ByteBuffer.wrap(udpHeader.mData);
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(buffer.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(buffer.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(buffer.get(IPHeader.PROTOCOL_OFFSET));
        short tcpLength = (short) (buffer.getShort(IPHeader.TOTAL_LEN_OFFSET) - udpHeader.mIpHeaderOffset);
        psdHeader.putShort(tcpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += (psdHeader.getShort() & 0xFFFF);
        }
        buffer.position(udpHeader.mIpHeaderOffset);
        buffer.limit(buffer.getShort(IPHeader.TOTAL_LEN_OFFSET));
        ByteBuffer udpBuffer = buffer.slice();
        while (udpBuffer.hasRemaining()) {
            try {
                checkSum += (udpBuffer.getShort() & 0xFFFF);
            } catch (Exception e) {
                checkSum += ((udpBuffer.get() << 8) & 0xFFFF);
            }
        }
        int packetCheckSum = udpHeader.mDataBuffer.getShort( CHECK_SUM_OFFSET) & 0xFFFF;
        checkSum -= packetCheckSum;
        while ((checkSum >> 16) > 0) {
            checkSum = ((checkSum >> 16) & 0xFFFF) + checkSum & 0xFFFF;
        }
        short sum = (short) ~(checkSum & 0xFFFF);
        return sum == packetCheckSum;
    }

    public ByteBuffer data() {
        return ByteBuffer.wrap(mData);
    }

    public byte[] dataByte() {
        return mData;
    }

    public int offset() {
        return mDataOffset;
    }


    @Override
    public String toString() {
        return "UDPHeader{" +
                "源端口 = " + (getSrcPort() & 0xFFFF) + ",\t" +
                "目标端口 = " + getDestPort() + ",\t" +
                "长度 = " + getTotalLength() + ",\t" +
                "校验和 = 0x" + Integer.toHexString(mDataBuffer.getShort(CHECK_SUM_OFFSET)) + "\t" +
                ", mIpHeaderOffset=" + mIpHeaderOffset +
                ", mDataOffset=" + mDataOffset +
                "}\n";
    }
}
