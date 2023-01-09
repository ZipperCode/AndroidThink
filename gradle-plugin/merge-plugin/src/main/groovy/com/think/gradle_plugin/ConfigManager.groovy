package com.think.gradle_plugin


import org.apache.http.util.TextUtils

import java.nio.file.Paths

class ConfigManager {
    static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager()
    }

    static ConfigManager getInstance() {
        return Holder.INSTANCE
    }

    boolean debug = false;

    String workRootDir = ""

    String versionMargeRootDir = ""

    private Map<String, String> oldClassMapper = new HashMap<>()
    private Map<String, String> newClassMapper = new HashMap<>()

    String oldClassRootDir

    String newClassRootDir

    private List<ClassObject> margeMapperList = new ArrayList<>()

    void loadMapper(File oldTableFile, File newTableFile) {
        oldClassRootDir = oldTableFile.absolutePath
        newClassRootDir = newTableFile.absolutePath
        oldTableFile = new File(oldTableFile, "table.txt")
        newTableFile = new File(newTableFile, "table.txt")
        if (oldTableFile.exists() && newTableFile.exists()) {
            oldClassMapper.clear()
            newClassMapper.clear()
            oldClassMapper.putAll(CommonUtils.readTableFile(oldTableFile))
            newClassMapper.putAll(CommonUtils.readTableFile(newTableFile))
        } else {
            throw new RuntimeException("oldTableFile = ${oldTableFile} , newTableFile = ${newTableFile}")
        }
    }

    void marge() {
        if (oldClassMapper.size() < 1 || newClassMapper.size() < 1) {
            throw new RuntimeException("请先调用loadMapper加载table.txt中的内容")
        }

        newClassMapper.each { key, value ->
            if (oldClassMapper.containsKey(key)) {
                def oldValue = oldClassMapper.get(key)
                if (debug) {
                    println "旧 ：${key} =  ${oldValue}"
                    println "新 ：${key} = ${value}"
                    println "是否相同 oldValue == value ${oldValue == value}"
                }
                // hash 值不一样，使用新版的路径
                if (!oldValue.equals(value)) {
                    ClassObject classObject = new ClassObject("$newClassRootDir${File.separator}$key",
                            value, CommonUtils.pathParseVersion(newClassRootDir))
                    margeMapperList.add(classObject)
                }
            } else {
                if (debug) {
                    println "=========== 旧版不包含 =========== ${key}"
                }
                ClassObject classObject = new ClassObject("$newClassRootDir${File.separator}$key",
                        value, CommonUtils.pathParseVersion(newClassRootDir))
                margeMapperList.add(classObject)
            }
        }

        oldClassMapper.clear()
        newClassMapper.clear()
        if (TextUtils.isEmpty(workRootDir)) {
            throw new RuntimeException("workRootDir == null or '' ")
        }

        // 生成新的文件地址
        if (margeMapperList.size() == 0) {
            println "没有文件更新，不需要合并"
            return
        }

        String margeVersion = "${CommonUtils.pathParseVersion(oldClassRootDir)}_${CommonUtils.pathParseVersion(newClassRootDir)}"

        margeMapperList.each { ClassObject co ->
            if(debug){
                println "co ==> ${co}"
            }
            File file = new File(co.absolutePath)
            if (!file.exists()) {
                if(debug){
                    println "${file.absolutePath} 不存在，请检查！！！"
                }
                return
            }
            File dest = Paths.get(workRootDir, versionMargeRootDir, margeVersion, co.path).toFile()
            CommonUtils.copyFile(file, dest)
//            FileUtils.copyFile(file,Paths.get(workRootDir,versionMargeRootDir,margeVersion,co.path))
        }

    }

    void clearMapper() {
        oldClassMapper.clear()
        newClassMapper.clear()
        oldClassRootDir = ""
        newClassRootDir = ""
    }


}