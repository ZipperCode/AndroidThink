package com.think.plugin.asm

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * @author  zhangzhipeng
 * @date    2022/7/12
 */
class AsmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("AsmPlugin apply")
        // 添加扩展类
        project.extensions.create("catchMethodExtension", CatchMethodExtension::class.java)

        // 拿到扩展类
        val catchExt = project.property("catchMethodExtension")

        println("扩展类为：$catchExt")
    }
}