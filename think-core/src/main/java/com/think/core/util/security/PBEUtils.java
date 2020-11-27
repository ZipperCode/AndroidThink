package com.think.core.util.security;

import java.security.GeneralSecurityException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PBEUtils {

    private static final String ALGORITHM = "PBEwithSHA1and128bitAES-CBC-BC";

    /**
     * 口令加密算法，将一个简单的密码通过和随机数的结合改造成安全的密文
     *
     * @param password 明文密码
     * @param salt     随机的salt（理解为随机的字）
     * @param input    用户数据
     * @param cycles   轮训次数
     * @return 加密结果
     * @throws GeneralSecurityException 异常
     */
    public static byte[] encrypt(char[] password, byte[] salt, byte[] input, int cycles) throws GeneralSecurityException {
        return crypt(true,password,salt,input,cycles);
    }

    public static byte[] decrypt(char[] password, byte[] salt, byte[] input, int cycles) throws GeneralSecurityException {
        return crypt(false, password, salt, input, cycles);
    }

    /**
     * 口令加密算法，将一个简单的密码通过和随机数的结合改造成安全的密文
     *
     * @param encrypt  是否加密
     * @param password 明文密码
     * @param salt     随机的salt（理解为随机的字）
     * @param input    用户数据
     * @param cycles   轮训次数
     * @return 加密结果
     * @throws GeneralSecurityException 异常
     */
    public static byte[] crypt(boolean encrypt, char[] password, byte[] salt, byte[] input, int cycles) throws GeneralSecurityException {
        // 使用明文生成PBE秘钥策略
        PBEKeySpec keySpec = new PBEKeySpec(password);
        // 通过秘钥工厂，使用PBE秘钥策略生成一个秘钥Key
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        // 随机数轮训，轮训次数越多越复杂
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, cycles);
        // 加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
        return cipher.doFinal(input);
    }

    public static void main(String[] args) {
    }
}
