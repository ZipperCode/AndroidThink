package com.think.gradle_plugin.ext

import org.gradle.api.Action

class ClassExtension{

    def buildWorkRootDir = "build/intermediates/custom"

    FixExtension fixExt = new FixExtension()

    void fix(Action<FixExtension> action){
        action.execute(fixExt)
    }

    def debug = true
}
