package com.think.core.util.security;

import com.think.core.util.DataTransformUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricUtil {

    private static final String TYPE_DES = "DES";
    private static final String TYPE_AES = "AES";
    private static final String TYPE_DES3 = "DESEDE";

    public static byte[] getSecretKey(String encryptType) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptType);
        keyGenerator.init(new SecureRandom());
        SecretKey generateKey = keyGenerator.generateKey();
        return generateKey.getEncoded();
    }

    public static byte[] encrypt(byte[] key, String plain, String type) {
        SecretKey secretKey = new SecretKeySpec(key, type);
        try {
            Cipher cipher = Cipher.getInstance(type);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(plain.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] decrypt(byte[] key, String text, String type) {
        SecretKey secretKey = new SecretKeySpec(key, type);
        try {
            Cipher cipher = Cipher.getInstance(type);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String desEncrypt(byte[] key, String plain) {
        return DataTransformUtil.byteToHexString(encrypt(key, plain, TYPE_DES));
    }

    public static String desDecrypt(byte[] key, String text) {
        return DataTransformUtil.byteToHexString(decrypt(key, text, TYPE_DES));
    }

    public static String des3Encrypt(byte[] key, String plain) {
        return DataTransformUtil.byteToHexString(encrypt(key, plain, TYPE_DES3));
    }

    public static String des3Decrypt(byte[] key, String text) {
        return DataTransformUtil.byteToHexString(decrypt(key, text, TYPE_DES3));
    }

    public static String aesEncrypt(byte[] key, String plain) {
        return DataTransformUtil.byteToHexString(encrypt(key, plain, TYPE_AES));
    }

    public static String aesDecrypt(byte[] key, String text) {
        return DataTransformUtil.byteToHexString(decrypt(key, text, TYPE_AES));
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        byte[] bs1 = getSecretKey(TYPE_DES);
        System.out.println(Arrays.toString(bs1));
        String s = DataTransformUtil.byteToHexString(bs1);
        System.out.println(s);
        byte[] bs2 = DataTransformUtil.hexString2byte(s);
        System.out.println(Arrays.toString(bs2));


    }
}
