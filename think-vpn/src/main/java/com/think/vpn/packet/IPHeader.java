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

    public ByteBuffer mData;
    public int mDataOffset = 20;

    public IPHeader(byte[] data, int offset) {
        this.mData = ByteBuffer.wrap(data);
        this.mDataOffset = offset;
    }

    public IPHeader(ByteBuffer data, int dataOffset) {
        this.mData = data;
        this.mDataOffset = dataOffset;
    }

    public IPHeader setVersion(int versionCode) {
        // 前四位清零
        byte clr = (byte) (mData.get(VER_NUM_AND_LEN_OFFSET) & 0x0F);
        // 填充版本号信息
        clr |= ((versionCode & 0xFF) << 4);
        this.mData.put(VER_NUM_AND_LEN_OFFSET, clr);
        return calcCheckSum();
    }

    public int getVersion() {
        return mData.get(VER_NUM_AND_LEN_OFFSET) >> 4;
    }

    public IPHeader setHeaderLength(int headerLength) {
        // 后四位清零
        byte clr = (byte) (mData.get(VER_NUM_AND_LEN_OFFSET) & 0xF0);
        clr |= ((headerLength & 0x0F));
        this.mData.put(VER_NUM_AND_LEN_OFFSET, clr);
        return calcCheckSum();
    }

    public int getHeaderLength() {
        return mData.get(VER_NUM_AND_LEN_OFFSET) & 0x0F;
    }

    public IPHeader setServiceType(int serviceType) {
        this.mData.put(SERVICE_TYPE_OFFSET, (byte) (serviceType & 0xFF));
        return calcCheckSum();
    }

    public int getServiceType() {
        return mData.get(SERVICE_TYPE_OFFSET);
    }

    public IPHeader setTotalLen(int totalLen) {
        this.mData.putShort(TOTAL_LEN_OFFSET, (short) (totalLen & 0xFFFF));
        return calcCheckSum();
    }

    public int getTotalLen() {
        return mData.getShort(TOTAL_LEN_OFFSET);
    }

    public IPHeader setIdentifier(int identifier) {
        this.mData.putShort(IDENTIFIER_OFFSET, (short) (identifier & 0xFFFF));
        return calcCheckSum();
    }

    public int getIdentifier() {
        return mData.getShort(IDENTIFIER_OFFSET);
    }

    public IPHeader setSliceFlag(int sliceFlag) {
        this.mData.put(SLICE_OFFSET, (byte) ((sliceFlag & 0xF) << 1));
        return calcCheckSum();
    }

    public int getSliceFlag() {
        return (mData.get(SLICE_OFFSET) >> 5);
    }

    public IPHeader setSlice(int slice) {
        byte temp = this.mData.get(SLICE_OFFSET);
        temp |= ((slice & 0x1FFF) >> 8);
        this.mData.put(SLICE_OFFSET, temp);
        this.mData.put(SLICE_OFFSET + 1, (byte) (slice & 0xFF));
        return calcCheckSum();
    }

    public int getSlice() {
        return (mData.getShort(SLICE_OFFSET) & 0x1FFF);
    }

    public IPHeader setTtl(int ttl) {
        this.mData.put(TTL_OFFSET, (byte) (ttl & 0xFF));
        return calcCheckSum();
    }

    public int getTtl() {
        return mData.get(TTL_OFFSET);
    }

    public IPHeader setProtocol(int protocol) {
        this.mData.put(PROTOCOL_OFFSET, (byte) (protocol & 0xFF));
        return calcCheckSum();
    }


    public int getProtocol() {
        return this.mData.get(PROTOCOL_OFFSET);
    }

    public synchronized IPHeader calcCheckSum() {
        // 计算IP头部校验和
        mData.putShort(CHECK_SUM_OFFSET, (short) 0);
        int oldLimit = mData.limit();
        mData.limit(getHeaderLength());
        mData.position(0);
        long checkSum = 0;
        while (mData.hasRemaining()) {
            try {
                checkSum += (mData.getShort() & 0xFFFF);
            } catch (Exception e) {
                mData.put((byte)0);
            }
        }
        while ((checkSum >> 16) > 0){
            checkSum = (checkSum >> 16) + (checkSum & 0xFFFF);
        }
        mData.limit(oldLimit);
        mData.putShort(CHECK_SUM_OFFSET, (short) ~(checkSum & 0xFFFF));
        return this;
    }

    public int getCheckSum() {
        return mData.getShort(CHECK_SUM_OFFSET);
    }

    public static boolean checkCheckSum(byte[] data) {
        ByteBuffer wrap = ByteBuffer.wrap(data);
        int checkSum = 0;
        while (wrap.hasRemaining()) {
            checkSum += (wrap.getShort() & 0xFFFF);
        }
        checkSum = ((checkSum >> 16) + (checkSum & 0xFFFF));
        int hight = checkSum >> 16;
        while (hight > 0) {
            checkSum += hight;
            hight = checkSum >> 16;
        }
        return (short) ~(checkSum & 0xFFFF) == 0x0000;
    }

    public IPHeader setSourceAddress(int sourceAddress) {
        this.mData.putInt(SRC_ADDRESS_OFFSET, sourceAddress);
        return calcCheckSum();
    }

    public int getSourceIpAddress() {
        return mData.getInt(SRC_ADDRESS_OFFSET);
    }


    public IPHeader setDestinationAddress(int destinationAddress) {
        this.mData.putInt(DEST_ADDRESS_OFFSET, destinationAddress);
        return calcCheckSum();
    }

    public int getDestAddress() {
        return mData.getInt(DEST_ADDRESS_OFFSET);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("IPHeader{").append("\r\n")
                .append("4位版本号 ：").append(CommonUtil.getIpVersion(getVersion())).append(",\t")
                .append("4为首部长度 ：").append(CommonUtil.getHeaderLength(getHeaderLength())).append("字节,\t")
                .append("8位服务类型：").append(Integer.toBinaryString(getServiceType())).append(",\t")
                .append("16位总长度：").append(getTotalLen()).append("字节\r\n")
                .append("16位标识：").append(getIdentifier()).append(", \t")
                .append("3位标志：").append(Integer.toBinaryString(getSliceFlag())).append(",\t")
                .append("13位片偏移：").append(getSlice()).append("\r\n")
                .append("8位生存时间：").append(getTtl()).append(", \t")
                .append("8位协议：").append(CommonUtil.getProtocolString(getProtocol())).append(",\t")
                .append("16位校验和：").append(getCheckSum()).append("\r\n")
                .append("32位源IP地址:").append(CommonUtil.int2Ip(getSourceIpAddress())).append("\r\n")
                .append("32位目标IP地址:").append(CommonUtil.int2Ip(getDestAddress())).append("\r\n")
                .append("}");
        return stringBuilder.toString();
    }

    public void fullDefault() {
        setHeaderLength(20);
        setServiceType((byte) 0);
        setTotalLen(0);
        setIdentifier(0);
        setSliceFlag((short) 0);
        setSliceFlag((short) 0);
        setTtl((byte) 64);
    }

    public static void main(String[] args) {
//        byte buff[] = new byte[]{0x45,0x00,0x00,0x31,
//                (byte)0x89, (byte) 0xF5,0x00,0x00,
//                0x6E,0x06,0x00,0x00,
//                (byte) 0xDE, (byte) 0xB7,0x45,0x5D,
//                (byte) 0xC0, (byte) 0xA8,0x00, (byte) 0xDC};
//        IPHeader ipHeader = new IPHeader(buff,20);
//        ipHeader.calcCheckSum();
//        byte[] data = ipHeader.getData();
//        System.out.println(IPHeader.checkCheckSum(data));

        byte[] datas = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        ByteBuffer wrap = ByteBuffer.wrap(datas);
        byte b = wrap.get();
        System.out.println(b);
        byte b1 = wrap.get();
        System.out.println(b1);
        byte b2 = wrap.get();
        System.out.println(b2);
        wrap.flip();
        System.out.println("rewind : ");
        while (wrap.hasRemaining()) {
            System.out.print(wrap.get());
        }
        System.out.println();


    }


}
