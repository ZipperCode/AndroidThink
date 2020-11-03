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

    private ByteBuffer mData;
    private int mIpHeaderOffset;
    private int mDataOffset;

    public TCPHeader(byte[] data, int dataOffset) {
        this.mData = ByteBuffer.wrap(data);
        this.mIpHeaderOffset = dataOffset;
        this.mDataOffset = mIpHeaderOffset + Packet.TCP_HEADER_SIZE;
    }

    public TCPHeader(ByteBuffer data, int ipHeaderOffset) {
        this.mData = data;
        this.mIpHeaderOffset = ipHeaderOffset;
        this.mDataOffset = mIpHeaderOffset + Packet.TCP_HEADER_SIZE;
    }

    public TCPHeader setSrcPort(int srcPort) {
        this.mData.putShort(mIpHeaderOffset + SRC_PORT_OFFSET, (short) (srcPort & 0xFFFF));
        return calcCheckSum();
    }


    public int getSrcPort() {
        int srcPort = mData.getShort(mIpHeaderOffset + SRC_PORT_OFFSET) & 0xFFFF;
        return srcPort;
    }

    public TCPHeader setDestPort(int destPort) {
        this.mData.putShort(mIpHeaderOffset + DEST_PORT_OFFSET, (short) (destPort & 0xFFFF));
        return calcCheckSum();
    }

    public int getDestPort() {
        int destPort = this.mData.getShort(mIpHeaderOffset + DEST_PORT_OFFSET) & 0xFFFF;
        return destPort;
    }

    public TCPHeader setSeqNo(int seqNo) {
        this.mData.putInt(mIpHeaderOffset + SEQ_NO_OFFSET, seqNo);
        return calcCheckSum();
    }

    public int getSeqNo() {
        int seqNo = this.mData.getInt(mIpHeaderOffset + SEQ_NO_OFFSET);
        return seqNo;
    }

    public TCPHeader setAckNo(int ackNo) {
        this.mData.putInt(mIpHeaderOffset + ACK_NO_OFFSET, ackNo);
        return calcCheckSum();
    }

    public int getAckNo() {
        int ackNo = this.mData.getInt(mIpHeaderOffset + ACK_NO_OFFSET);
        return ackNo;
    }

    public TCPHeader setHeaderLength(int headerLength){
        int len = headerLength * 8 / 32;
        this.mData.put(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET, (byte) ((len & 0xF) << 4));
        return calcCheckSum();
    }

    public TCPHeader setFlag(TcpFlag tcpFlag){
        // 前两位保留，后六位为标志位
        byte flag = mData.get(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET + 1);
        if(tcpFlag.URG) flag |= 0x20; // 0010 0000
        if(tcpFlag.ACK) flag |= 0x10; // 0001 0000
        if(tcpFlag.PSH) flag |= 0x08; // 0000 1000
        if(tcpFlag.RST) flag |= 0x04; // 0000 0100
        if(tcpFlag.SYN) flag |= 0x02; // 0000 0010
        if(tcpFlag.FIN) flag |= 0x01; // 0000 0001
        this.mData.put(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET + 1, flag);
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
        this.mData.put(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET, (byte) ((len & 0xF) << 4));
        this.mData.put(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET + 1, (byte) flag);
        return calcCheckSum();
    }

    public int getHeaderLen() {
        return ((mData.get(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET) >> 4) & 0x0F) * 32 / 8;
    }

    public int getFlag() {
        return this.mData.get(mIpHeaderOffset + HEADER_LEN_AND_FLAG_OFFSET + 1);
    }

    public TCPHeader setWindowSize(int windowSize) {
        this.mData.putShort(mIpHeaderOffset + WINDOW_SIZE_OFFSET, (short) (windowSize & 0xFFFF));
        return calcCheckSum();
    }

    public int getWindowSize() {
        int size = this.mData.getShort(mIpHeaderOffset + WINDOW_SIZE_OFFSET) & 0xFFFF;
        return size;
    }

    public synchronized TCPHeader calcCheckSum() {
        // 首先校验位置零
        this.mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET, (short) 0);
        long checkSum = 0;

        // 伪首部12字节 源地址、目标地址、标志、协议、tcp长度
        ByteBuffer psdHeader = ByteBuffer.allocate(12);
        psdHeader.putInt(this.mData.getInt(IPHeader.SRC_ADDRESS_OFFSET));
        psdHeader.putInt(this.mData.getInt(IPHeader.DEST_ADDRESS_OFFSET));
        psdHeader.put((byte) 0);
        psdHeader.put(this.mData.get(IPHeader.PROTOCOL_OFFSET));
        short tcpLength = (short) (this.mData.getShort(IPHeader.TOTAL_LEN_OFFSET) - Packet.IP4_HEADER_SIZE);
        psdHeader.putShort(tcpLength);
        psdHeader.flip();
        // 伪首部累加
        while (psdHeader.hasRemaining()) {
            checkSum += psdHeader.getShort();
        }
        // 从ip头部偏移开始
        this.mData.position(mIpHeaderOffset);
        // 数据累加
        while (mData.hasRemaining()) {
            try {
                checkSum += mData.getShort();
            } catch (Exception e) {
                checkSum += (mData.get() << 8);
            }
        }
        while ((checkSum >> 16) > 0){
            checkSum = (checkSum >> 16) + checkSum & 0xFFFF;
        }
        short sum = (short) ~(checkSum & 0xFFFF);
        mData.putShort(mIpHeaderOffset + CHECK_SUM_OFFSET, sum);
        return this;
    }

    public int getCheckSum() {
        return mData.getShort(mIpHeaderOffset + CHECK_SUM_OFFSET) & 0xFFFF;
    }

    public static boolean checkCrc(TCPHeader tcpHeader){
        int checkSum = 0;
        tcpHeader.mData.position(tcpHeader.mIpHeaderOffset);
        ByteBuffer buffer = tcpHeader.mData;
        ByteBuffer tcpBuffer = buffer.slice();
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

        while (tcpBuffer.hasRemaining()) {
            try{
                checkSum += tcpBuffer.getShort();
            }catch (Exception e){
                tcpBuffer.put((byte)0);
            }
        }
        while ((checkSum >> 16) > 0){
            // 前十六位和后十六位相加
            checkSum = (checkSum >> 16) + checkSum & 0xFFFF;
        }

        short sum = (short) ~(checkSum & 0xFFFF);
        short packetCheckSum =  tcpHeader.mData.getShort(tcpHeader.mIpHeaderOffset + CHECK_SUM_OFFSET);
        return sum == packetCheckSum;
    }

    public TCPHeader setUrgentPoint(int urgentPoint) {
        mData.putShort(mIpHeaderOffset + URGENT_POINTER_OFFSET, (short) urgentPoint);
        return this;
    }

    public int getUrgentPointer() {
        return mData.getShort(mIpHeaderOffset + URGENT_POINTER_OFFSET) & 0xFFFF;
    }

    public ByteBuffer data() {
        return mData;
    }

    public int offset() {
        return mIpHeaderOffset;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TCPHeader {").append("\r\n")
                .append("源端口为：").append(getSrcPort()).append(",")
                .append("目的端口为：").append(getDestPort()).append("\r\n")
                .append("序列号为：").append(getSeqNo()).append("\r\n")
                .append("确认号为：").append(getAckNo()).append("\r\n")
                .append("TCP首部长度为：").append(getHeaderLen()).append(",")
                .append("标志位：[");
        int flag = getFlag();
        stringBuilder.append("URG=").append((flag & URG) == 0 ? 0 : 1).append(",")
                .append("ACK=").append((flag & ACK) == 0 ? 0 : 1).append(",")
                .append("PSH=").append((flag & PSH) == 0 ? 0 : 1).append(",")
                .append("RST=").append((flag & RST) == 0 ? 0 : 1).append(",")
                .append("SYN=").append((flag & SYN) == 0 ? 0 : 1).append(",")
                .append("FIN=").append((flag & FIN) == 0 ? 0 : 1).append("],");

        stringBuilder.append("窗口大小：").append(getWindowSize()).append("\r\n")
                .append("校验和：").append(getCheckSum()).append(",")
                .append("紧急指针：").append(getUrgentPointer()).append("\r\n}");


        return stringBuilder.toString();
    }

    public static class TcpFlag{
        public boolean URG;
        public boolean ACK;
        public boolean PSH;
        public boolean RST;
        public boolean SYN;
        public boolean FIN;

    }
}
