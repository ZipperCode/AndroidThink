package com.think.vpn.utils;

public class CommonUtil {

    public static int ip2Int(String ip) {
        String[] arrStrings = ip.split("\\.");
        int r = (Integer.parseInt(arrStrings[0]) << 24)
                | (Integer.parseInt(arrStrings[1]) << 16)
                | (Integer.parseInt(arrStrings[2]) << 8)
                | Integer.parseInt(arrStrings[3]);
        return r;
    }

    public static String int2Ip(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp).append(" ");
        }
        return sb.toString();
    }

    public static String getIpVersion(int version) {
        return version == 4 ? "IPv4" : "IPv6";
    }

    public static int getHeaderLength(int len) {
        return 4 * len;
    }

    public static String getSliceFlag(int sliceFlog) {
        return Integer.toBinaryString(sliceFlog);
    }

    public static String getProtocolString(int protocol) {
        switch (protocol) {
            case 1:
                return "ICMP";
            case 6:
                return "TCP";
            case 17:
                return "UDP";
            default:
                return "Other";
        }
    }

    public static String getDnsQueryType(short queryType) {
        switch (queryType) {
            case 1:
                return "A";
            case 2:
                return "NS";
            case 5:
                return "CNAME";
            case 6:
                return "SOA";
            case 11:
                return "WKS";
            case 12:
                return "PTR";
            case 13:
                return "HINFO";
            case 15:
                return "MX";
            case 28:
                return "AAAA";
            case 252:
                return "AXFR";
            case 255:
            default:
                return "ANY";
        }
    }


    public static void main(String[] args) {
        System.out.println(getSliceFlag(2));
    }
}
