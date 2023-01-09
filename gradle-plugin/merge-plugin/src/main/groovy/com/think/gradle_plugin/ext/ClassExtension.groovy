package com.think.gradle_plugin.ext

import org.gradle.api.Action

/**
 * Class下的扩展参数
 */
class ClassExtension{
    /**
     * 构建的类目录
     */
    def buildWorkRootDir = "build/intermediates/custom"
    /**
     * Fix扩展参数
     */
    FixExtension fixExt = new FixExtension()

    void fix(Action<FixExtension> action){
        action.execute(fixExt)
    }
    /**
     * true 表示使用debug模式
     */
    def debug = true
}
