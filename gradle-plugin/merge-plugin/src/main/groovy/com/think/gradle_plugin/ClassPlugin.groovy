package com.think.gradle_plugin

import com.android.build.gradle.AppExtension
import com.think.gradle_plugin.ext.ClassExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class ClassPlugin implements Plugin<Project> {

    Project project;

    ClassExtension clsExt;

    @Override
    void apply(Project project) {
        println("CustomPlugin apply\r\n")
        this.project = project
        clsExt = project.extensions.create(Constants.CLS_EXT,ClassExtension)
        // 配置初始化参数
        init()
        // 配置任务
        configTask()
        // 配置transform
        configTransform()
        // 任务监听
        project.gradle.taskGraph.beforeTask { Task task ->
            if(task.name == "transformClassesWithClassTransformForDebug"){
                def startTime = System.currentTimeMillis()
                task.doFirst{
                    println "beforeTask doFirst ==> $task.name "
                }

                task.doLast {
                    println "------------总共耗时 ${System.currentTimeMillis() - startTime} 毫秒--------------"
                    println "beforeTask doLast ==> $task.name"
                }
            }
        }
    }

    void init(){
        // 配置全局参数
        ConfigManager.getInstance().debug = clsExt.debug
        ConfigManager.getInstance().workRootDir = project.projectDir.absolutePath
        ConfigManager.getInstance().versionMargeRootDir = clsExt.fixExt.versionMargeRootDir
    }

    void configTask(){
        // 创建一个拷贝生成类的任务
        project.tasks.create("copyClasses", CopyClassesTask.class){
            projectRef project
            group = "cus"
        }
        // 创建一个合并任务
        project.tasks.create("margeClass", MargeTask.class){
            projectRef project
            group "cus"
            dependsOn("copyClasses")
        }
    }

    void configTransform(){
        def android = project.extensions.getByType(AppExtension)
        // 注册一个Transform
        def classTransform = new ClassTransform(project)
        android.registerTransform(classTransform)
    }

}