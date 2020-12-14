package com.think.gradle_plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import think.gradle_plugin.ext.ClassExtension

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
        def buildVersionClassDir = "$classExtension.buildWorkRootDir/$classExtension.fixExt.classRootDir/$classExtension.fixExt.versionCode"
        def versionClassDir = new File(rootDir,"${classExtension.fixExt.versionCode}")
        if(classExtension.fixExt.compareVersionCover){
            FileUtils.deleteRecursivelyIfExists(versionClassDir)
        }
        if(versionClassDir.exists()){
            println("本地存在版本与当前版本相同，禁止覆盖，如需覆盖则设置 DSL compareVersionCover=true")
           return
        }
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