package com.think.core.util.security;

import com.think.core.util.DataTransformUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageUtil {

    private static final String TYPE_MD5 = "MD5";
    private static final String TYPE_SHA_1 = "SHA";
    private static final String TYPE_SHA_256 = "SHA-256";


    public static String messageDigestCrypt(String string, String cryptType)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(cryptType);
        byte[] result = digest.digest(string.getBytes()); //digest中会执行一次update
        return DataTransformUtil.byteToHexString(result);
    }

    public static String md5Crypt(String plain){
        try {
            return messageDigestCrypt(plain, TYPE_MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String shaCrypt(String plain){
        try {
            return messageDigestCrypt(plain, TYPE_SHA_1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sha256Crypt(String plain){
        try {
            return messageDigestCrypt(plain, TYPE_SHA_256);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
