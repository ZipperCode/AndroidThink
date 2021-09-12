package com.think.xposed.crypto;

import com.think.xposed.Utils;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.cert.Certificate;

import javax.crypto.spec.IvParameterSpec;

public class SymmetricBean {
    String mInstanceParam;

    int mInitCryptMode;

    Key mInitSecretKey;

    String mInitSecretKeyEncodeHex;

    String mInitSecretKeySerial;

    IvParameterSpec mInitIvParameterSpec;

    String mInitIvParameterSpecHex;

    Certificate mInitCertificate;

    String mInitCertificateSerial;

    byte[] mUpdateData;

    int mUpdateOffset;

    int mUpdateLength;

    ByteBuffer mUpdateInputBuffer;

    ByteBuffer mUpdateOutputBuffer;

    byte[] mDoFinalData;

    int mDoFinalOffset;

    int mDoFinalLength;

    byte[] mDoFinalResult;


    @Override
    public String toString() {
        return new StringBuilder().append("\n")
                .append("算法transformation为：").append(mInstanceParam).append("\n")
                .append("算法模式为：").append(mInitCryptMode).append("(1 -- 加密， 2 -- 解密)").append("\n")
                .append("算法Key为(Encode)：").append(mInitSecretKeyEncodeHex).append("\n")
                .append("算法Key为(Serial)：").append(mInitSecretKeySerial).append("\n")
                .append("算法IV向量为(Hex)：").append(mInitIvParameterSpecHex).append("\n")
                .append("证书为(Serial)：").append(mInitCertificateSerial).append("\n")
                .append("update方法添加的参数为：").append(mUpdateData != null ? mUpdateLength == 0 ?
                        Utils.byteHexToString(mUpdateData) :
                        Utils.byteHexToString(mUpdateData, mUpdateOffset, mUpdateLength) : "null").append("\n")
                .append("update方法添加的参数字符为：").append(mUpdateData != null ? mUpdateLength == 0 ?
                        new String(mUpdateData) :
                        new String(mUpdateData, mUpdateOffset, mUpdateLength) : "null").append("\n")
                .append("mUpdateInputBuffer:").append(mUpdateInputBuffer).append("\n")
                .append("mUpdateOutputBuffer:").append(mUpdateOutputBuffer).append("\n")
                .append("doFinal方法参数为：").append(mDoFinalData != null ?
                        mDoFinalLength == 0 ? Utils.byteHexToString(mDoFinalData) :
                                Utils.byteHexToString(mDoFinalData, mDoFinalOffset, mDoFinalLength) : "null").append("\n")
                .append("doFinal方法参数字符为：[").append(mDoFinalData != null ? mDoFinalLength == 0 ?
                        new String(mDoFinalData) :
                        new String(mDoFinalData, mDoFinalOffset, mDoFinalLength) : "null").append("]").append("\n")
                .append("doFinal返回的结果为：").append(Utils.byteHexToString(mDoFinalResult)).append("\n")
                .append("doFinal返回的结果字符为：[").append(new String(mDoFinalResult)).append("]")
                .toString();
    }
}
