package com.think.gradle_plugin.task

import com.think.gradle_plugin.ext.ClassExtension
import com.think.gradle_plugin.ext.FixExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

/**
 * 合并任务
 */
class MargeTask extends DefaultTask{

    private Project project

    @TaskAction
    void run(){
        println ">>>> MargeTask task start running !"
        ClassExtension classExtension = project.extensions.getByName(Constants.CLS_EXT)
        FixExtension fix = classExtension.fixExt
        // 旧版本目录
        File oldFile = Paths.get(project.projectDir.absolutePath, fix.versionClassRootDir,fix.margeOldVersion).toFile()
        // 新版本目录
        File newFile = Paths.get(project.projectDir.absolutePath, fix.versionClassRootDir,fix.margeNewVersion).toFile()

        println "oldFile = ${oldFile.absolutePath}"
        println "newFile = ${newFile.absolutePath}"

        if(!oldFile.exists() || !newFile.exists()){
            throw new RuntimeException("合并的旧版本目录或者新版本目录为空，操作取消")
        }
        // 加载table.txt的内容，因为后续需要进行文件md5的比对
        ConfigManager.getInstance().loadMapper(oldFile,newFile)
        // 进行合并
        ConfigManager.getInstance().marge()
        println ">>>> MargeTask task run end !"
    }

    void setProjectRef(Project project){
        this.project = project
    }

    Project getProjectRef(){
        return this.project
    }
}