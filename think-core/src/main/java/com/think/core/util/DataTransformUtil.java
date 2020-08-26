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
}
