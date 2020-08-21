package com.think.core.net.okhttp;

import java.io.File;

public class OtherUtils {
    public static String downloadRename(String srcFileName){
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

}
