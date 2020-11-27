package com.think.core.util.security;


import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UnSymmetricUtil {

    private static final String TYPE_RSA = "RSA";

    public static Pair<byte[], byte[]> getSecretKey(String encryptType, int len) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(encryptType);
        keyPairGenerator.initialize(len);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey aPrivate = keyPair.getPrivate();
        keyPair.getPublic()
        byte[] pri = keyPair.getPrivate().getEncoded();
        byte[] pub = keyPair.getPublic().getEncoded();
        return new Pair<>(pri, pub);
    }


    private static byte[] priCrypt(boolean encrypt, byte[] priKey, byte[] input, String type) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance(type);
        PKCS8EncodedKeySpec skSpec = new PKCS8EncodedKeySpec(priKey);
        PrivateKey privateKey = keyFactory.generatePrivate(skSpec);
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(input);
    }

    private static byte[] pubCrypt(boolean encrypt, byte[] pubKey, byte[] input, String type) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance(type);
        X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(pubKey);
        PublicKey publicKey = keyFactory.generatePublic(pkSpec);
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(input);
    }


    public static byte[] priEncrypt(byte[] priKey, byte[] input, String type)  {
        try{
            return priCrypt(true,priKey,input,type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] priDecrypt(byte[] priKey, byte[] input, String type) throws Exception {
        try{
            return priCrypt(false,priKey,input,type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] pubEncrypt(byte[] pubKey, byte[] input, String type) {
        try{
            return pubCrypt(true,pubKey,input,type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] pubDecrypt(byte[] pubKey, byte[] input, String type) {
        try{
            return pubCrypt(false,pubKey,input,type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Pair<byte[], byte[]> rsa = getSecretKey("RSA", 512);

        byte[] bytes = priEncrypt(rsa.first, "hello world".getBytes(), "RSA");

        byte[] rsas = pubDecrypt(rsa.second, bytes, "RSA");

        System.out.println(new String(rsas));

    }

    static class Pair<F,S>{
        F first;
        S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}
