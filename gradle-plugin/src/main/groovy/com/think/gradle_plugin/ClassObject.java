package com.think.gradle_plugin;

import java.util.Objects;

/**
 * 保存类的一些文件信息
 */
public class ClassObject {
    /**
     * 构建当前类工程的版本
     */
    String version;
    /**
     * 类名，全限定名
     */
    String className;
    /**
     * 路径，类文件存放的目录
     */
    String path;
    /**
     * 绝对目录
     */
    String absolutePath;
    /**
     * 文件的md5值，方便不同版本的比对
     */
    String md5;

    public ClassObject(String absolutePath, String md5){
        this(absolutePath,md5,"1.0");
    }

    public ClassObject(String absolutePath, String md5,String version){
        int classIndex = absolutePath.lastIndexOf(version) + version.length();
        this.path =  absolutePath.substring(classIndex + 1);
        this.md5 = md5;
        this.version = version;
        this.className = absolutePath.substring(classIndex + 1)
                .replaceAll("\\\\",".").replace("/",".");
        this.absolutePath = absolutePath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassObject that = (ClassObject) o;
        return version.equals(that.version) &&
                className.equals(that.className) &&
                path.equals(that.path) &&
                absolutePath.equals(that.absolutePath) &&
                md5.equals(that.md5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, className, path, absolutePath, md5);
    }

    @Override
    public String toString() {
        return "ClassObject{" +
                "version='" + version + '\'' +
                ", className='" + className + '\'' +
                ", path='" + path + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
