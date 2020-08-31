package com.think.core.util.security;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author : zzp
 * @date : 2020/8/31
 **/
public class SimpleUtils {

    public static final String TYPE_BASE64 = "Base64";

    private static final char[] BASE64_INDEX_TABLE = new char[] { 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };
    /**
     * 实际数据一个字节8位，如果按照索引表则对应的字符用6位即可表示，所以取字节数和表对应字符数位数
     * 取最大公倍数 8 和 6 对应 24 也就是说三个字符对应base64 四个字符
     * 根据base64 索引表，一个字节数据8位，
     * @param data 数据
     * @return 结果
     */
    public static String base64Encode(byte[] data){
        StringBuilder stringBuilder = new StringBuilder(data.length);
        int index = 0;
        while (index < data.length){
            int sInt = data[index] << 16;
            if(index == data.length - 1){
                stringBuilder
                        .append(BASE64_INDEX_TABLE[(byte)(sInt >> 18) & 0x3F])
                        .append(BASE64_INDEX_TABLE[(byte)((sInt >> 12) & 0x3F)])
                        .append("==");
                break;
            }else if(index == data.length - 2){
                sInt |= (data[index + 1] & 0xFF) << 8;
                stringBuilder
                        .append(BASE64_INDEX_TABLE[(byte)(sInt >> 18) & 0x3F])
                        .append(BASE64_INDEX_TABLE[(byte)(sInt >> 12) & 0x3F])
                        .append(BASE64_INDEX_TABLE[(byte)((sInt >> 6) & 0x3F)])
                        .append("=");
                break;
            }else{
                sInt |= (data[index + 1] & 0xFF) << 8 |
                        (data[index + 2] & 0xFF);
                stringBuilder
                        .append(BASE64_INDEX_TABLE[(byte)(sInt >> 18) & 0x3F])
                        .append(BASE64_INDEX_TABLE[(byte)(sInt >> 12) & 0x3F])
                        .append(BASE64_INDEX_TABLE[(byte)(sInt >> 6) & 0x3F])
                        .append(BASE64_INDEX_TABLE[(byte)(sInt& 0x3F)]);
            }
            index += 3;
        }
        return stringBuilder.toString();
    }

    public static byte[] base64Decode(String text){
        int index = 0;
        while (index < text.length()){


            index += 4;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        String hello  = "heoodsfasdfasd";
        System.out.println("bytes length = " + hello.getBytes().length);
        System.out.println(Base64.getEncoder().encodeToString(hello.getBytes()));
        System.out.println(base64Encode(hello.getBytes()));
    }
}
