package com.think.gradle_plugin;

import java.util.Objects;

public class ClassObject {

    String version;

    String className;

    String path;

    String absolutePath;

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
