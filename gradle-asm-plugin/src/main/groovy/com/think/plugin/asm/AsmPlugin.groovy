
package com.think.plugin.asm

import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmPlugin implements Plugin<Project>{

    @Override
    void apply(Project target) {
        println "AsmPlugin apply"
    }
}