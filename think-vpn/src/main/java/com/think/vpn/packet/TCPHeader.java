package com.think.vpn.packet;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;


public class TCPHeader {

    /**
     * 结束连接 00 0001
     */
    public static final int FIN = 0x01;
    /**
     * 发起连接 00 0010
     */
    public static final int SYN = 0x02;
    /**
     * 重建连接 00 0100
     */
    public static final int RST = 0x04;
    /**
     * 接收方应该尽快将这个报文段交给应用层。 00 1000
     */
    public static final int PSH = 0x08;
    /**
     * 确认序列号有效 01 0000
     */
    public static final int ACK = 0x10;
    /**
     * 紧急指针 10 0000
     */
    public static final int URG = 0x20;
    /**
     * 源端口,16位
     */
    public static final int SRC_PORT_OFFSET = 0;
    /**
     * 目的端口，16位
     */
    public static final int DEST_PORT_OFFSET = 2;
    /**
     * 32位序号
     * 一次TCP通信过程中某一个传输方向上的字节流的每个字节的编号，通过这个来<br>
     * 确认发送的数据有序，比如现在序列号为1000，发送了1000，下一个序列号就是2000。
     */
    public static final int SEQ_NO_OFFSET = 4;
    /**
     * 32 位确认号
     * 用来响应TCP报文段，给收到的TCP报文段的序号加1，三握时还要携带自己的序号。
     */
    public static final int ACK_NO_OFFSET = 8;
    /**
     * 4位头部长度、6位保留字、6位标志位
     * URG（紧急指针是否有效）ACK（表示确认号是否有效）PSH（提示接收端应用<br>
     * 程序应该立即从TCP接收缓冲区读走数据）RST（表示要求对方重新建立连接）SYN（表示请求建立一个连接）FIN（表示通知对方本端要关闭连接）
     */
    public static final int HEADER_LEN_AND_FLAG_OFFSET = 12;
    /**
     * 窗口大小 16位
     * TCP流量控制的一个手段，用来告诉对端TCP缓冲区还能容纳多少字节。
     */
    public static final int WINDOW_SIZE_OFFSET = 14;
    /**
     * 校验和 16位
     * 由发送端填充，接收端对报文段执行CRC算法以检验TCP报文段在传输中是否损坏。
     */
    public static final int CHECK_SUM_OFFSET = 16;
    /**
     * 紧急指针 16位
     * 一个正的偏移量，它和序号段的值相加表示最后一个紧急数据的下一字节的序号。
     */
    public static final int URGENT_POINTER_OFFSET = 18;

    /**
     * 选项
     */
    public static final int OPTION = 20;

    private final byte[] mData;
    private final ByteBuffer mDataBuffer;
    public final int mIpHeaderOffset;
    public final int mDataOffset;
    public int mSize;

    public TCPHeader(byte[] data, int dataOffset) {
        this.mData = data;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.mIpHeaderOffset = dataOffset;
        buffer.position(mIpHeaderOffset);
        this.mDataBuffer = buffer.slice();
        this.mDataOffset = mIpHeaderOffset + Packet.TCP_HEADER_SIZE;
    }


    public TCPHeader setSrcPort(int srcPort) {
        this.mDataBuffer.putShort(SRC_PORT_OFFSET, (short) (srcPort & 0xFFFF));
        return calcCheckSum();
    }

    public int getSrcPort() {
        int srcPort = mDataBuffer.getShort(SRC_PORT_OFFSET) & 0xFFFF;
        return srcPort;
    }

    public TCPHeader setDestPort(int destPort) {
        this.mDataBuffer.putShort(DEST_PORT_OFFSET, (short) (destPort & 0xFFFF));
        return calcCheckSum();
    }

    public int getDestPort() {
        int destPort = this.mDataBuffer.getShort( DEST_PORT_OFFSET) & 0xFFFF;
        return destPort;
    }

    public TCPHeader setSeqNo(int seqNo) {
        this.mDataBuffer.putInt( SEQ_NO_OFFSET, seqNo);
        return calcCheckSum();
    }

    public int getSeqNo() {
        int seqNo = this.mDataBuffer.getInt( SEQ_NO_OFFSET);
        return seqNo;
    }

    public TCPHeader setAckNo(int ackNo) {
        this.mDataBuffer.putInt( ACK_NO_OFFSET, ackNo);
        return calcCheckSum();
    }

    public int getAckNo() {
        int ackNo = this.mDataBuffer.getInt( ACK_NO_OFFSET);
        return ackNo;
    }

    public TCPHeader setHeaderLength(int headerLength) {
        int len = headerLength * 8 / 32;
        this.mDataBuffer.put( HEADER_LEN_AND_FLAG_OFFSET, (byte) ((len & 0xF) << 4));
        return calcCheckSum();
    }

    public TCPHeader setFlag(TcpFlag tcpFlag) {
        // 前两位保留，后六位为标志位
        byte flag = mDataBuffer.get( HEADER_LEN_AND_FLAG_OFFSET + 1);
        if (tcpFlag.URG) flag |= 0x20; // 0010 0000
        if (tcpFlag.ACK) flag |= 0x10; // 0001 0000
        if (tcpFlag.PSH) flag |= 0x08; // 0000 1000
        if (tcpFlag.RST) flag |= 0x04; // 0000 0100
        if (tcpFlag.SYN) flag |= 0x02; // 0000 0010
        if (tcpFlag.FIN) flag |= 0x01; // 0000 0001
        this.mDataBuffer.put( HEADER_LEN_AND_FLAG_OFFSET + 1, flag);
        return calcCheckSum();
    }

    /**
     * 头部4位
     *
     * @param flag URG | ACK [| PSH | RST | SYN | FIN]
     */
    public TCPHeader setHeaderLenAndFlag(int flag) {
        //设置头部长度为20字节,设置为5
        // 20 * 8 = 160 / 32 = 5
        int len = Packet.TCP_HEADER_SIZE * 8 / 32;
        this.mDataBuffer.put( HEADER_LEN_AND_FLAG_OFFSET, (byte) ((len & 0xF) << 4));
        this.mDataBuffer.put( HEADER_LEN_AND_FLAG_OFFSET + 1, (byte) flag);
        return calcCheckSum();
    }

    public int getHeaderLen() {
        return ((mDataBuffer.get( HEADER_LEN_AND_FLAG_OFFSET) >> 4) & 0x0F) * 32 / 8;
    }

    public int getFlag() {
        return this.mDataBuffer.get( HEADER_LEN_AND_FLAG_OFFSET + 1);
    }

    public TCPHeader setWindowSize(int windowSize) {
        this.mDataBuffer.putShort( WINDOW_SIZE_OFFSET, (short) (windowSize & 0xFFFF));
        return calcCheckSum();
    }

    public int getWindowSize() {
        int size = this.mDataBuffer.getShort( WINDOW_SIZE_OFFSET) & 0xFFFF;
        return size;
    }

    public synchronized TCPHeader calcCheckSum() {
        // 首先校验位置零
        this.mDataBuffer.putShort( CHECK_SUM_OFFSET, (short) 0);
        long checkSum = 0;
        ByteBuffer buffer = ByteBuffer.wrap(mData);
        // 伪首部12字节 源地址、目标地址、标志、协议、tcp长度
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(buffer.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(buffer.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(buffer.get(IPHeader.PROTOCOL_OFFSET));
        short tcpLength = (short) (buffer.getShort(IPHeader.TOTAL_LEN_OFFSET) - mIpHeaderOffset);
        psdHeader.putShort(tcpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += psdHeader.getShort() & 0xFFFF;
        }
        buffer.position(mIpHeaderOffset);
        buffer.limit(mDataBuffer.getShort(IPHeader.TOTAL_LEN_OFFSET));
        ByteBuffer tcpBuffer = buffer.slice();
        // 数据累加
        while (tcpBuffer.hasRemaining()) {
            try {
                checkSum += tcpBuffer.getShort() & 0xFFFF;
            } catch (Exception e) {
                checkSum += (tcpBuffer.get() << 8) & 0xFFFF;
            }
        }
        while ((checkSum >> 16) > 0) {
            checkSum = ((checkSum >> 16) & 0xFFFF) + checkSum & 0xFFFF;
        }
        short sum = (short) ~(checkSum & 0xFFFF);
        mDataBuffer.putShort( CHECK_SUM_OFFSET, sum);
        return this;
    }

    public void setCheckSum(short checkSum) {
        mDataBuffer.putShort( CHECK_SUM_OFFSET, checkSum);
    }

    public int getCheckSum() {
        return mDataBuffer.getShort( CHECK_SUM_OFFSET) & 0xFFFF;
    }

    public synchronized static boolean checkCrc(TCPHeader tcpHeader) {
        long checkSum = 0;
        ByteBuffer newBuffer = ByteBuffer.wrap(tcpHeader.mData);
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(newBuffer.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(newBuffer.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(newBuffer.get(IPHeader.PROTOCOL_OFFSET));
        short tcpLength = (short) (newBuffer.getShort(IPHeader.TOTAL_LEN_OFFSET) - tcpHeader.mIpHeaderOffset);
        psdHeader.putShort(tcpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += psdHeader.getShort() & 0xFFFF;
        }
        newBuffer.position(tcpHeader.mIpHeaderOffset);
        ByteBuffer tcpBuffer = newBuffer.slice();
        while (tcpBuffer.hasRemaining()) {
            try {
                checkSum += tcpBuffer.getShort() & 0xFFFF;
            } catch (Exception e) {
                checkSum += ((tcpBuffer.get() << 8) & 0xFFFF);
            }
        }
        checkSum -= tcpHeader.getCheckSum();
        while ((checkSum >> 16) > 0) {
            // 前十六位和后十六位相加
            checkSum = (checkSum >> 16) + checkSum & 0xFFFF;
        }

        short sum = (short) ~(checkSum & 0xFFFF);
        short packetCheckSum = tcpHeader.mDataBuffer.getShort( CHECK_SUM_OFFSET);
        return sum == packetCheckSum;
    }

    public TCPHeader setUrgentPoint(int urgentPoint) {
        mDataBuffer.putShort( URGENT_POINTER_OFFSET, (short) urgentPoint);
        return this;
    }

    public int getUrgentPointer() {
        return mDataBuffer.getShort(URGENT_POINTER_OFFSET) & 0xFFFF;
    }

    public void size(int size){
        this.mSize = size;
        this.mDataBuffer.limit(size);
    }

    public ByteBuffer data() {
        return ByteBuffer.wrap(mData);
    }

    public byte[] bytes(){
        return mData;
    }

    public int offset() {
        return mIpHeaderOffset;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("TCPHeader {").append("\r\n")
//                .append("源端口为：").append(getSrcPort()).append(",\t")
//                .append("目的端口为：").append(getDestPort()).append(",\t")
//                .append("序列号为：").append(getSeqNo()).append(",\t")
//                .append("确认号为：").append(getAckNo()).append(",\t")
//                .append("TCP首部长度为：").append(getHeaderLen()).append(",\t")
//                .append("标志位：[");
//        int flag = getFlag();
//        stringBuilder.append("URG=").append((flag & URG) == 0 ? 0 : 1).append(",")
//                .append("ACK=").append((flag & ACK) == 0 ? 0 : 1).append(",")
//                .append("PSH=").append((flag & PSH) == 0 ? 0 : 1).append(",")
//                .append("RST=").append((flag & RST) == 0 ? 0 : 1).append(",")
//                .append("SYN=").append((flag & SYN) == 0 ? 0 : 1).append(",")
//                .append("FIN=").append((flag & FIN) == 0 ? 0 : 1).append("],\t");
//
//        stringBuilder.append("窗口大小：").append(getWindowSize()).append("\t")
//                .append("校验和：").append(getCheckSum()).append(",")
//                .append("紧急指针：").append(getUrgentPointer())
//                .append(" }");
        stringBuilder.append("TCPHeader >> ").append("srcPort = ").append(getSrcPort()).append("->")
                .append("destPort = ").append(getDestPort());
        return stringBuilder.toString();
    }

    public static class TcpFlag {
        public boolean URG;
        public boolean ACK;
        public boolean PSH;
        public boolean RST;
        public boolean SYN;
        public boolean FIN;
    }
}
