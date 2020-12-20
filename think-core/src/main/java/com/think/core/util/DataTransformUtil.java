package com.think.core.util;

public class DataTransformUtil {

    public static String byteToHexString(byte data[]) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte datum : data) {
            stringBuffer.append(String.format("%02X", datum & 0xFF));
        }
        return stringBuffer.toString();
    }

    public static byte[] hexString2byte(String text) {
        byte[] bytes = new byte[text.length() / 2];
        for (int i = 0; i < text.length() / 2; i ++) {
            bytes[i] = (byte)Integer.parseInt(text.substring(i* 2 , i*2 + 2),16);
        }
        return bytes;
    }

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
}
