package com.think.gradle_plugin.task

import com.think.gradle_plugin.ext.ClassExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

/**
 * 拷贝类文件的任务
 */
class CopyClassesTask extends DefaultTask{

    private Project project
    private ClassExtension classExtension;
    private File rootDir;

    @TaskAction
    void run(){
        classExtension = project.extensions.getByName(Constants.CLS_EXT)
        rootDir = new File(project.projectDir,classExtension.fixExt.versionClassRootDir)
        if(!rootDir.exists()){
            rootDir.mkdirs()
        }
        // 构建的版本的类文件目录
        def buildVersionClassDir = "$classExtension.buildWorkRootDir/$classExtension.fixExt.classRootDir/$classExtension.fixExt.versionCode"
        // 构建版本号类目录
        def versionClassDir = new File(rootDir,"${classExtension.fixExt.versionCode}")
        // 判断标志，是否可以覆盖，
        if(classExtension.fixExt.compareVersionCover){
            FileUtils.deleteRecursivelyIfExists(versionClassDir)
        }
        if(versionClassDir.exists()){
            println("本地存在版本与当前版本相同，禁止覆盖，如需覆盖则设置 DSL compareVersionCover=true")
           return
        }
        // 拷贝目录到另一个目录
        FileUtils.copyDirectoryContentToDirectory(
                new File(project.projectDir,buildVersionClassDir),
                versionClassDir
        )
    }

    void setProjectRef(Project project){
        this.project = project
    }

    Project getProjectRef(){
        return this.project
    }
}