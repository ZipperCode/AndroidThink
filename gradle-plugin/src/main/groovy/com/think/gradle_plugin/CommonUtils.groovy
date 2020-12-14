package com.think.gradle_plugin

import org.apache.commons.compress.archivers.zip.ZipFile

import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

class CommonUtils {
    static def calcMd5(File file) {
        if (!file.exists()) {
            return ""
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5")
        file.eachByte 4096, { bytes, size ->
            messageDigest.update(bytes, 0, size)
        }
        return messageDigest.digest().collect { String.format("%02X", it) }.join()
    }

    static def buildFileMd5Table(File rootDir, Map<String, String> tables) {
        File tableFile = new File(rootDir, 'table.txt')
        if (!rootDir.exists()) {
            throw new RuntimeException("rootDir not exists!!!")
        }
        if (!tableFile.exists()) {
            tableFile.createNewFile()
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(tableFile))
        rootDir.traverse {
            if (it.isFile()) {
                def md5 = calcMd5(it)
                tables.put(it.absolutePath, md5)
                bw.writeLine("${it.absolutePath.substring(rootDir.absolutePath.length() + 1)}=${md5}")
                if(ConfigManager.getInstance().debug){
                    println "write success ==>  ${it.absolutePath.substring(rootDir.absolutePath.length() + 1)}=$md5"
                }
            }
        }
        bw.flush()
        bw.close()
    }

    static def buildFileMd5Table(File rootDir) {
        File tableFile = new File(rootDir, 'table.txt')
        if (!rootDir.exists()) {
            throw new RuntimeException("rootDir not exists!!!")
        }
        if (!tableFile.exists()) {
            tableFile.createNewFile()
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(tableFile))
        rootDir.traverse {
            if (it.isFile() && !it.absolutePath.contains("META-INF")) {
                def md5 = calcMd5(it)
                bw.writeLine("${it.absolutePath.substring(rootDir.absolutePath.length() + 1)}=${md5}")
                if(ConfigManager.getInstance().debug){
                    println "write success ==>  ${it.absolutePath.substring(rootDir.absolutePath.length() + 1)}=$md5"
                }
            }
        }
        bw.flush()
    }

    static def readTableFile(File tableFile) {
        Map<String, String> map = new HashMap<>();
        BufferedReader bw = new BufferedReader(new FileReader(tableFile))
        String line = null
        while ((line = bw.readLine()) != null) {
            def keyValue = line.split("=")
            map.put(keyValue[0], keyValue[1])
        }
        bw.close()
        return map
    }

    static def checkAndCreateDir(dirPath) {
        File file = new File(dirPath)
        if (file.isFile()) {
            file = file.getParentFile()
        }
        if (!file.exists()) {
            file.mkdirs()
        }
    }


    static def unzipFile(File dir, File file) {
        def zipFile = new ZipFile(file)
        zipFile.getEntries().each { it ->
            def path = Paths.get(dir.absolutePath + File.separator+ it.name)
            if(it.directory){
                Files.createDirectories(path)
            } else {
                def parentDir = path.getParent()
                if (!Files.exists(parentDir)) {
                    Files.createDirectories(parentDir)
                }
                if(Files.exists(path)){
                    Files.deleteIfExists(path)
                }
                Files.copy(zipFile.getInputStream(it), path)
            }
        }
        zipFile.close()
    }

    static def copyFile(File src, File dest){
        if(src.exists()){
            if(!dest.exists()){
                if(!dest.parentFile.exists()){
                    dest.parentFile.mkdirs()
                }
                dest.createNewFile()
            }
            FileInputStream fis = null;
            FileOutputStream fos = null
            try{
                fis = new FileInputStream(src)
                fos = new FileOutputStream(dest)
                long transferred = 0;
                long size = fis.channel.size();
                while(transferred != size){
                    transferred += fis.channel.transferTo(0,size,fos.channel);
                }
            }catch(IOException e){
                e.printStackTrace()
            }finally{
                if(fis != null){
                    fis.close()
                }
                if(fos != null){
                    fos.close()
                }
            }
        }else{
            System.err.println("src file exists = ${src.exists()} , dest file exists = ${dest.exists()}")
        }
    }

    static String pathParseVersion(String path){
        return path.substring(path.lastIndexOf(File.separator) + 1)
    }
}