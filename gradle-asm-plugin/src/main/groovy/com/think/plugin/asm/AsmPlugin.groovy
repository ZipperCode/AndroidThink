
package com.think.plugin.asm

import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println "AsmPlugin apply"
        // 添加扩展类
        project.getExtensions().create("catchMethodExtension", CatchMethodExtension.class)

        // 拿到扩展类
        def catchExt = project.property("catchMethodExtension")

        println "扩展类为：$catchExt"
    }
}