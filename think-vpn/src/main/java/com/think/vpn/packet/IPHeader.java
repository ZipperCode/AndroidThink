package com.think.vpn.packet;

import com.think.vpn.utils.CommonUtil;

import java.nio.ByteBuffer;

public class IPHeader {

    public static final byte IP4 = 0x04;
    public static final byte IP6 = 0x06;
    /**
     * 常用协议类型
     */
    public static final int PROTOCOL_ICMP = 1;
    public static final int PROTOCOL_TCP = 6;
    public static final int PROTOCOL_UDP = 17;
    /**
     * 版本号和头部长度。4字节版本号，4字节长度
     * IP协议（IPv4）版本号位4
     * 标识头部有多少个4字节，即最大共15*4个字节
     */
    public static final int VER_NUM_AND_LEN_OFFSET = 0;
    /**
     * 服务类型。
     * 包含一个4位优先权字段：最小延时，最大吞吐量，最高可靠性和最小费用。
     */
    public static final int SERVICE_TYPE_OFFSET = 1;
    /**
     * 整个数据包的长度。
     * 表示整个IP数据报的长度，最大表示65535，但由于MTU限制，一般无法到达这个值。
     */
    public static final int TOTAL_LEN_OFFSET = 2;
    /**
     * 唯一的标识数据报。系统采用加1的式边发送边赋值。
     * 系统用+1法为每个数据报唯一标识此位，如果数据13被分片，所有分的小片中此位都是13
     */
    public static final int IDENTIFIER_OFFSET = 4;
    /**
     * 前三位标识分片标识 （保留，DF禁止分片，MF更多分片）：所以这个标志是为分片存在，<br>
     * DF设置时禁止分片所以如果数据报太大则发送失败。MF设置时，如果产生分片，除了<br>
     * 最后一个分片，其他此片置1。
     * 后十三位为分片偏移 分片相对原始IP数据报开始处的偏移。
     */
    public static final int SLICE_OFFSET = 6;
    /**
     * 生存时间 ttl
     * 数据报到达目的地之前允许经过的路由跳跳数。跳一下减1，得0丢弃。
     */
    public static final int TTL_OFFSET = 8;
    /**
     * 协议
     * 用来区分上层协议（ICMP为1，TCP为6，UDP为17）。
     */
    public static final int PROTOCOL_OFFSET = 9;
    /**
     * 头部校验和
     * 仅以CRC算法检验数据报头部在传输过程中是否损坏。
     */
    public static final int CHECK_SUM_OFFSET = 10;
    /**
     * 源端口地址
     */
    public static final int SRC_ADDRESS_OFFSET = 12;
    /**
     * 目标端口地址
     */
    public static final int DEST_ADDRESS_OFFSET = 16;

    private byte[] mData;
    public ByteBuffer mHeaderBuffer;
    public int mDataOffset = 20;
    public int mSize;

    public IPHeader(byte[] data, int offset) {
        this.mData = data;
        this.mHeaderBuffer = ByteBuffer.wrap(data);
        this.mDataOffset = offset;
        this.mHeaderBuffer.limit(mDataOffset);
    }

    public IPHeader setVersion(int versionCode) {
        // 前四位清零
        byte clr = (byte) (mHeaderBuffer.get(VER_NUM_AND_LEN_OFFSET) & 0x0F);
        // 填充版本号信息
        clr |= ((versionCode & 0x0F) << 4);
        this.mHeaderBuffer.put(VER_NUM_AND_LEN_OFFSET, clr);
        return calcCheckSum();
    }

    public int getVersion() {
        return mHeaderBuffer.get(VER_NUM_AND_LEN_OFFSET) >> 4;
    }

    public IPHeader setHeaderLength(int headerLength) {
        byte hl = (byte)(headerLength * 8 / 32);
        // 后四位清零
        byte clr = (byte) (mHeaderBuffer.get(VER_NUM_AND_LEN_OFFSET) & 0xF0);
        clr |= ((hl & 0x0F));
        this.mHeaderBuffer.put(VER_NUM_AND_LEN_OFFSET, clr);
        return calcCheckSum();
    }

    public int getHeaderLength() {
        return mHeaderBuffer.get(VER_NUM_AND_LEN_OFFSET) & 0x0F;
    }

    public IPHeader setServiceType(int serviceType) {
        this.mHeaderBuffer.put(SERVICE_TYPE_OFFSET, (byte) (serviceType & 0xFF));
        return calcCheckSum();
    }

    public int getServiceType() {
        return mHeaderBuffer.get(SERVICE_TYPE_OFFSET) & 0xFF;
    }

    public IPHeader setTotalLen(int totalLen) {
        this.mHeaderBuffer.putShort(TOTAL_LEN_OFFSET, (short) (totalLen & 0xFFFF));
        return calcCheckSum();
    }

    public int getTotalLen() {
        return mHeaderBuffer.getShort(TOTAL_LEN_OFFSET) & 0xFFFF;
    }

    public IPHeader setIdentifier(int identifier) {
        this.mHeaderBuffer.putShort(IDENTIFIER_OFFSET, (short) (identifier & 0xFFFF));
        return calcCheckSum();
    }

    public int getIdentifier() {
        return mHeaderBuffer.getShort(IDENTIFIER_OFFSET);
    }

    public IPHeader setSliceFlag(int sliceFlag) {
        this.mHeaderBuffer.put(SLICE_OFFSET, (byte) ((sliceFlag & 0xF) << 5));
        return calcCheckSum();
    }

    public int getSliceFlag() {
        return (mHeaderBuffer.get(SLICE_OFFSET) >> 5);
    }

    public IPHeader setSlice(int slice) {
        byte temp = this.mHeaderBuffer.get(SLICE_OFFSET);
        temp &= 0xE0; // 0000 0000 1110 0000 取出前三位
        slice &= 0x1FFF; // 设置后十三位 num & 0001 1111 1111 1111
        temp <<= 8; // 1110 0000 0000 0000
        this.mHeaderBuffer.putShort((short) (temp | slice));
        return calcCheckSum();
    }

    public int getSlice() {
        return mHeaderBuffer.getShort(SLICE_OFFSET) & 0x1FFF;
    }

    public IPHeader setTtl(int ttl) {
        this.mHeaderBuffer.put(TTL_OFFSET, (byte) (ttl & 0xFF));
        return calcCheckSum();
    }

    public int getTtl() {
        return mHeaderBuffer.get(TTL_OFFSET);
    }

    public IPHeader setProtocol(int protocol) {
        this.mHeaderBuffer.put(PROTOCOL_OFFSET, (byte) (protocol & 0xFF));
        return calcCheckSum();
    }

    public int getProtocol() {
        return this.mHeaderBuffer.get(PROTOCOL_OFFSET);
    }

    public synchronized IPHeader calcCheckSum() {
        // 计算IP头部校验和
        mHeaderBuffer.putShort(CHECK_SUM_OFFSET, (short) 0);
        ByteBuffer newBuffer = ByteBuffer.wrap(mData);
        newBuffer.position(0);
        newBuffer.limit(mDataOffset);
        long checkSum = 0;
        while (newBuffer.hasRemaining()) {
            try {
                checkSum += (newBuffer.getShort() & 0xFFFF);
            } catch (Exception e) {
                checkSum += ((newBuffer.get() << 8) & 0xFFFF);
            }
        }
        while ((checkSum >> 16) > 0){
            checkSum = ((checkSum >> 16) & 0xFFFF) + (checkSum & 0xFFFF);
        }
        mHeaderBuffer.putShort(CHECK_SUM_OFFSET, (short) ~(checkSum & 0xFFFF));
        return this;
    }
    public void setCheckSum(short checkSum) {
        mHeaderBuffer.putShort(CHECK_SUM_OFFSET,checkSum);
    }

    public int getCheckSum() {
        return mHeaderBuffer.getShort(CHECK_SUM_OFFSET) & 0xFFFF;
    }

    public IPHeader setSourceAddress(int sourceAddress) {
        this.mHeaderBuffer.putInt(SRC_ADDRESS_OFFSET, sourceAddress);
        return calcCheckSum();
    }

    public int getSourceIpAddress() {
        return mHeaderBuffer.getInt(SRC_ADDRESS_OFFSET);
    }


    public IPHeader setDestinationAddress(int destinationAddress) {
        this.mHeaderBuffer.putInt(DEST_ADDRESS_OFFSET, destinationAddress);
        return calcCheckSum();
    }

    public int getDestAddress() {
        return mHeaderBuffer.getInt(DEST_ADDRESS_OFFSET);
    }

    public synchronized static boolean checkCrc(IPHeader ipHeader){
        ByteBuffer buffer = ByteBuffer.wrap(ipHeader.mData);
        long originCheckSum = ipHeader.getCheckSum() & 0xFFFF;
        buffer.position(0);
        buffer.limit(ipHeader.mDataOffset);
        long checkSum = 0;
        while (buffer.hasRemaining()) {
            try {
                checkSum += (buffer.getShort() & 0xFFFF);
            } catch (Exception e) {
                checkSum += ((buffer.get() << 8) & 0xFFFF);
            }
        }
        checkSum -= originCheckSum;
        while ((checkSum >> 16) > 0){
            checkSum = ((checkSum >> 16) & 0xFFFF) + (checkSum & 0xFFFF);
        }
        int res =  (short) ~(checkSum & 0xFFFF);
        return res == originCheckSum;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder
//                .append("IPHeader{").append("\r\n")
//                .append("4位版本号 ：").append(CommonUtil.getIpVersion(getVersion())).append(",\t")
//                .append("4为首部长度 ：").append(CommonUtil.getHeaderLength(getHeaderLength())).append(",\t")
//                .append("8位服务类型：").append(Integer.toBinaryString(getServiceType())).append(",\t")
//                .append("16位总长度：").append(getTotalLen()).append(",\t")
//                .append("16位标识：").append(getIdentifier()).append(",\t")
//                .append("3位标志：").append(Integer.toBinaryString(getSliceFlag())).append(",\t")
//                .append("13位片偏移：").append(getSlice()).append(",\t")
//                .append("8位生存时间：").append(getTtl()).append(",\t")
//                .append("8位协议：").append(CommonUtil.getProtocolString(getProtocol())).append(",\t")
//                .append("16位校验和：").append(Integer.toHexString(getCheckSum())).append(",\t")
//                .append("32位源IP地址:").append(CommonUtil.int2Ip(getSourceIpAddress())).append(",\t")
//                .append("32位目标IP地址:").append(CommonUtil.int2Ip(getDestAddress())).append(",\t")
//                .append("}\r\n");
        stringBuilder.append("IPHeader >> ").append(CommonUtil.getProtocolString(getProtocol())).append(":")
                .append(CommonUtil.int2Ip(getSourceIpAddress())).append("->").append(CommonUtil.int2Ip(getDestAddress()));
        return stringBuilder.toString();
    }



    public void fullDefault() {
        setHeaderLength(20);
        setServiceType((byte) 0);
        setTotalLen(Packet.IP4_HEADER_SIZE);
        setIdentifier(0);
        setSliceFlag((short) 0);
        setProtocol(IP4);
        setTtl((byte) 64);
    }
}
