package com.think.xposed;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    public static String rename(String srcFileName){
        String parentPath = new File(srcFileName).getParent();
        System.out.println("parentPath = " + parentPath);
        return appendFileName(srcFileName,0);
    }

    public static String appendFileName(String srcFileName, int index){
        File file = new File(srcFileName);
        if(!file.exists()){
            return srcFileName;
        }
        String fileName = file.getName();
        String parentPath = file.getParent();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = fileName.substring(0,fileName.lastIndexOf("."));
        if(fileName.matches("(.+)\\(\\d*\\)")){
            fileName = fileName.replaceAll("\\(\\d*\\)","(" + (++index) + ")");
        }else{
            fileName = fileName + "(" +(++index)+ ")";
        }
        return appendFileName(parentPath + File.separator + fileName +"."+ ext,index);
    }

    public static String byteHexToString(byte [] data){
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        for (byte b : data){
            stringBuilder.append(String.format("%02X ",b));
        }
        return stringBuilder.toString();
    }


    public static String byteHexToString(byte [] data,int offset, int len){
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        int length = offset + len;
        for (int i = offset; i < offset + len && length < data.length; i++) {
            stringBuilder.append(String.format("%02X",data[i]));
        }
        return stringBuilder.toString();
    }

    public static void writeData(String fileName, byte data[]){
        File file = new File("/data/data/" + AndroidAppHelper.currentPackageName() + "/files",fileName);
    }
}
